// 공통 기능

var commonLib = commonLib ?? {};

/**
*  Meta Tag 정보 조회
*  mode - rootUrl : <meta name="rootUrl" ... />
*  layouts_main.html<meta name="rootUrl" th:content="@{/}">
*
*/
commonLib.getMeta = function(mode) {

    if (!mode) return;

    // `` (역괄호, esc 아래) 사용시 변수를 ${} 안에 넣을 수 있음
    const el = document.querySelector(`meta[name = '${mode}']`)

    // el이 없으면 undefined
    // Optional Chaining 문법(최신)
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
 * @params callback : 응답 완료 후 후속 처리 콜백 함수
 * @params data : 요청 Data(Body 있을때만 가능, POST / PUT / PATCH)
 * @params headers : 추가 요청 Header
 * @param isText : true면 text false면 JSON
 * @returns {Promise<unknown>}
 */
commonLib.ajaxLoad = function(url, callback, method = 'GET', data, headers, isText = false) {

    if (!url) return;

    const { getMeta } = commonLib;
    // Header Meta Tag 쪽 데이터 get
    const csrfHeader = getMeta("_csrf_header");
    const csrfToken = getMeta("_csrf");
    // 외부 주소인 http || https 일 경우 : 아닐 경우
    url = /^http[s]?:/.test(url) ? url : getMeta("rootUrl") + url.replace("/", "");

    headers = headers ?? {};
    headers[csrfHeader] = csrfToken;
    method = method.toUpperCase();

    const options = {

        method,
        headers,
    }

    // GET, DELETE 는 Body 없으므로 요청 method 보고 판단해 경우에 맞게 처리
    if (data && ['POST', 'PUT', 'PATCH'].includes(method)) { // body 쪽 Data 추가 가능

        // formDate 일 경우 그대로 넘기고, 아닐경우 JSON 문자열로 가공 / JSON.stringify(변수명)
        options.body = data instanceof FormData ? data : JSON.stringify(data);
    }

    return new Promise((resolve, reject) => {

    /* Promise S */

    fetch(url, options)
        .then(res => {
        // 204가 아닐때에만 JSON 형태로 변환
        if (res.status !== 204)

            return isText ? res.text() : res.json();

        else {
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
            // ?. = 없으면 undefined
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
    const layerEls = document.querySelectorAll(".layer-dim", ".layer-popup");

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

    button.addEventListener("click", commonLib.popupClose);

    /* 레이어 팝업 닫기 버튼 추가 E */

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