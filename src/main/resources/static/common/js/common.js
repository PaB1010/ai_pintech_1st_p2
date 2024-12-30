// 공통 기능

// 중복되지 않을 일반적이지 않은 이름 NameSpace 할당
// var 선언자는 재선언이 가능하기때문에 const 가 아닌 var 사용
var commonLib = commonLib ?? {};

/**
*  Meta Tag 정보 조회
*  Meta 태그 : 사이트의 정보성 데이터를 담은 태그
*
*  mode - rootUrl : <meta name="rootUrl" ... />
*  layouts_main.html<meta name="rootUrl" th:content="@{/}">
*
*/
commonLib.getMeta = function(mode) {

    if (!mode) return;

    // `` (역괄호) 사용시 변수를 ${} 안에 넣을 수 있음
    const el = document.querySelector(`meta[name = '${mode}']`)

    // el null일 경우 undefined
    // Optional Chaining 연산자 문법
    return el?.content;
};

/**
 * JavaScript 에서 만든 주소에 Context 경로 추가한 형태로 가공
 *
 */
commonLib.url = function(url) {

    return `${commonLib.getMeta('rootUrl').replace("/","")}${url}`;
}

/**
 *  Ajax 요청 처리 함수
 *
 * @params url : (필수)요청 주소 / http[s] : 외부 URL - Context path 추가 X
 * @params method : 요청 방식 - GET / POST / DELETE / PATCH ...
 * @params callback : 응답 성공시 후속 처리 콜백 함수
 * @params data : Request(요청) Body Data(Body 있을때만 가능, POST / PUT / PATCH)
 * @params headers : 추가 Request(요청) Header
 * @param isText : true 면 text false 면 JSON
 * @returns {Promise<unknown>}
 */
commonLib.ajaxLoad = function(url, callback, method = 'GET', data, headers, isText = false) {

    if (!url) return;

    const { getMeta } = commonLib;
    // Header Meta Tag 쪽 데이터 get
    const csrfHeader = getMeta("_csrf_header");
    const csrfToken = getMeta("_csrf");
    // 외부 주소인 http || https 일 경우 : 아닐 경우
    // 외부 주소면 url 그대로 사용, 서버쪽 주소면 url 가공
    url = /^http[s]?:/.test(url) ? url : commonLib.url(url);

    headers = headers ?? {};
    headers[csrfHeader] = csrfToken;
    method = method.toUpperCase();

    // 토큰을 실어 보냄
    const options = {

        method,
        headers,
    }

    // GET, DELETE 는 Body 없으므로 요청 method 보고 판단해 경우에 맞게 처리
    if (data && ['POST', 'PUT', 'PATCH'].includes(method)) {
        // body 쪽 Data 추가 가능

        // FormDate 형태일 경우 그대로 넘기고, 객체 형태일 경우 JSON 문자열로 직렬화 가공 / JSON.stringify(변수명)
        options.body = data instanceof FormData ? data : JSON.stringify(data);
    }

    // 성공시 처리와 실패시 처리를 나누고, 순서를 유지하기 위해 Promise 로 감쌈
    return new Promise((resolve, reject) => {

    /* Promise S */

    fetch(url, options)
        // .then 서버가 응답했을때
        .then(res => {
        // 204가 아닐때에만 JSON 형태로 변환
        if (res.status !== 204)

            return isText ? res.text() : res.json();

        else {
            // 204 = NoContent 즉 Body 없으므로 바로 반환
                resolve();
            }
        })
        .then(json => {

            if (isText) {

                resolve(json);

                return;
            }
            // global_rests_JSONData.java
            // 응답(처리) 성공시
            // ?. = 없으면 JSON 그대로 resolve
            if (json?.success) {

                // callback 함수가 정의된 경우 열린 기능으로 각각 다르게 처리
                if (typeof callback === 'function') {

                    callback(json.data);
                }

                resolve(json);

                return;
            }
            // 처리 실패
            reject(json);
        })
        .catch (err => {

            console.error(err);

           // 응답 실패
           reject(err);
        });
    }); /* Promise E */
};

/**
 * 레이어 팝업
 *
 * @param url
 * @param width
 * @param height
 * @param isAjax
 */
commonLib.popup = function(url, width = 350, height = 350, isAjax = false) {

    /* 레이어 팝업 요소 동적 추가 S */
    // layer-dim : 배경
    // layer-popup : 팝업

    const layerEls = document.querySelectorAll(".layer-dim, .layer-popup");

    // 이미 있을 경우 대비해 호출되자마자 한번 제거해 비우고
    layerEls.forEach(el => el.parentElement.removeChild(el));

    const layerDim = document.createElement("div");
    layerDim.className = "layer.-dim";

    const layerPopup = document.createElement("div");
    layerPopup.className = "layer-popup";

    /* 레이어 팝업 가운데 배치 S */
    const xpos = (innerWidth - width) / 2;
    const ypos = (innerHeight - height) / 2;

    layerPopup.style.left = xpos + "px";
    layerPopup.style.top = ypos + "px";
    layerPopup.style.width = width + "px";
    layerPopup.style.height = height + "px";
    /* 레이어 팝업 가운데 배치 E */

    /* 레이어 팝업 컨텐츠 영역 추가 */

    const content = document.createElement("div");

    content.className = "layer-content";

    layerPopup.append(content);

    /* 레이어 팝업 닫기 버튼 추가 S */
    const button = document.createElement("button");
    const icon = document.createElement("i");

    button.className = "layer-close";
    button.type = "button";

    icon.className = "xi-close";

    button.append(icon);
    layerPopup.prepend(button);

    // 클릭시 제거되도록 Event-Binding
    button.addEventListener("click", commonLib.popupClose);
    /* 레이어 팝업 닫기 버튼 추가 E */

    // Body 에 추가
    document.body.append(layerDim);
    document.body.append(layerPopup);
    /* 레이어 팝업 요소 동적 추가 E */

    /* 팝업 컨텐츠 로드 S */
    if (isAjax) {
        // 컨텐츠를 ajax 로 로드
        const { ajaxLoad } = commonLib;

        ajaxLoad(url, null, 'GET', null, null, true)
            .then((text) => content.innerHTML = text)

    } else {
        // 컨텐츠를 iframe 으로 동적 로드
        const iframe = document.createElement("iframe");

        iframe.width = width - 80;
        iframe.height = height - 80;

        iframe.frameBorder = 0;

        iframe.src = commonLib.url(url);

        content.append(iframe);
    }
    /* 팝업 컨텐츠 로드 E */
}

/**
 * 레이어 팝업 제거
 *
 */
commonLib.popupClose = function() {

    const layerEls = document.querySelectorAll(".layer-dim, .layer-popup");

    layerEls.forEach(el => el.parentElement.removeChild(el));
}

/**
 * checkBox 전체 토글
 *
 */
window.addEventListener("DOMContentLoaded", function() {

    // checkBox 전체 토글 기능 S
    const checkAlls = document.getElementsByClassName("check-all");

    for (const el of checkAlls) {

        el.addEventListener("click", function() {

            const { targetClass } = this.dataset;

            if (!targetClass) { // 토글할 체크박스의 클래스가 설정되지 않은 경우는 진행 X
                return;
            }

            const chks = document.getElementsByClassName(targetClass);

            for (const chk of chks) {

                chk.checked = this.checked;
            }
        });
    }
    // checkBox 전체 토글 기능 E

    /* 팝업 버튼 클릭 처리 S */
    const showPopups = document.getElementsByClassName("show-popup");

    for( const el of showPopups) {

        el.addEventListener("click", function() {

            const { url, width, height } = this.dataset;
            
            commonLib.popup(url, width, height);
        });
    }
    /* 팝업 버튼 클릭 처리 E */
});