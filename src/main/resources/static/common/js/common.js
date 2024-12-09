// 공통적인 부분
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
*  Ajax 요청 처리 함수
*
* @params url : (필수)요청 주소 / http[s] : 외부 URL - Context path 추가 X
* @params method : 요청 방식 - GET / POST / DELETE / PATCH ...
* @params callback : 응답 완료 후 후속 처리 콜백 함수
* @params data : 요청 Data(Body가 있을때만 가능, POST / PUT / PATCH)
* @params headers : 추가 요청 Header
*/
commonLib.ajaxLoad = function(url, callback, method = 'GET', data, headers) {

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

    // GET, DELETE는 Body가 없으므로 요청 method 보고 판단해 경우에 맞게 처리
    if (data && ['POST', 'PUT', 'PATCH'].includes(method)) { // body 쪽 Data 추가 가능

        // formDate 일 경우 그대로 넘기고, 아닐경우 JSON 문자열로 가공 / JSON.stringify(변수명)
        options.body = data instanceof FormData ? data : JSON.stringify(data);
    }

    fetch(url, options)
        .then(res => res.json())
        .then(json => {

            // global_rests_JSONData.java
            // 응답(처리) 성공시
            if (json.success) {

                // callback 함수가 정의된 경우 열린 기능으로 각각 다르게 처리
                if (typeof callback === 'function') {

                    callback(json.data);
                }
                return;
            }
            // 응답(처리) 실패시 메세지 출력
            alert(json.message);
        })
        .catch (err => console.error(err))
};

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
});