window.addEventListener("DOMContentLoaded", function() {

    const sendButton = document.getElementById("send-auth-code");

    const authCodeEl = document.getElementById("auth-code");

    const verifyButton = document.getElementById("verify-auth-code");

    const timerEl = document.querySelector(".auth-box .timer");

    const { emailAuth } = commonLib;

    // email 있을 때에만 sendButton 활성화
    // email 없으면 sendButton 비활성화
    frmJoin.email.addEventListener("change", function() {

        if (this.value.trim()) {

            sendButton.disabled = false;

        } else {

            sendButton.disabled = true;
        }
    });

    // Enter key 입력해도 가입버튼 처리 안되도록 초기화
    frmJoin.addEventListener("keydown", function(e) {
            if (e.key == "Enter") {
              e.preventDefault();
            }
          });

    /* 인증 코드 전송 처리 S */

    sendButton.addEventListener("click", function() {

        const email = frmJoin.email.value.trim();

        if (!email) return;

        /**
        *   인증 코드 발급 완료 후 후속 처리
        *
        *   1. 전송 버튼의 문구를 "인증코드 재전송"으로 변경
        *   1-1. 한번 전송시 수신 이메일을 변경하지 못하도록 처리
        *   1-2. 인증 코드 입력 가능하게 처리
        *   1-3. 인증하기 버튼 노출
        *   2. 타이머 남은 시간 출력
        */

        emailAuth.sendCode(email, updateTimer, function() {

            // 1. 전송 버튼의 문구를 "인증코드 재전송"으로 변경
            const { text } = sendButton.dataset;

            sendButton.innerText = text;


            // 1-1. 한번 전송시 수신 이메일을 변경하지 못하도록 처리
            frmJoin.email.setAttribute("readonly", true)

            // 1-2. 인증 코드 입력 가능하게 처리
            authCodeEl.disabled = false;

            // 1-3. 인증하기 버튼 노출
            verifyButton.classList.remove("dn");

            authCodeEl.addEventListener("change", function() {
                let code = this.value.trim();

                code = code.replace(/\D/g, '');
                this.value = code;

            });
        });
    });



    /**
     * 타이머 출력 갱신
     *
     */
     function updateTimer(seconds) {

        // 변경되니까 let
        let timeStr = "";

        if (seconds > 0) {
            // 분 & 초 분리

            // 분 = 60 나눈 후 나머지 버림
            const min = Math.floor(seconds / 60)
            // 초
            const sec = seconds - min * 60;

            // padStart(2, '0')
            // 한자리 정수일때 앞에 0 채워 넣기
            timeStr = `${('' + min).padStart(2, '0')}:${('' + sec).padStart(2, '0')}`;

        } else { // 타이머가 0이 되면

            timeStr = "00:00";

            // 다시 이메일 변경 가능하게 처리
            formJoin.email.removeAttribute("readonly");

            // 인증 코드 값 비워주고
            authCodeEl.value = "";

            // 인증 코드 입력 불가 처리
            authCodeEl.disabled = true;

            // 인증하기 버튼 감추기
            verifyButton.classList.remove("dn");
            verifyButton.classList.add("dn");
        }

        if (timerEl) {

            // 주기적으로 남은 시간 변경
            timerEl.innerHTML = timeStr;
        }
     }
     /* 인증 코드 전송 처리 E */

     /* 인증 코드 확인 처리 S */

     // 입력 받은 인증 코드가 5글자를 넘어가지 못하도록 제안
         authCodeEl.addEventListener("keyup", function() {
                 let value = '' + this.value;
                 value = value.length > 5 ? value.substring(0, 5) : value;
                 this.value = value;
             });

     verifyButton.addEventListener("click", function() {

        // 인증 코드 검증
        const authCode = authCodeEl.value;

        // ('' + authCode) -> 간단하게 authCode 문자화
        if (!authCode || ('' + authCode).length < 5) {

            const message = authCode > 0 && ('' + authCode).length < 5 ? "인증코드는 5자리로 입력하세요." : "인증코드를 입력하세요";

            alert(message);

            authCodeEl.focus();

            return;
        }

        // 하나만 선택이니 querySelector
        const el = document.querySelector(".auth-box .message");
        el.classList.remove("dn");

        emailAuth.verify(authCode, () => {
            // 성공시 콜백

            /**
             * 1. "인증되었습니다." 메세지 출력
             * 2. authCodeEl, verifyButton, sendButton, timer 제거
             */
             el.innerText = "인증되었습니다.";

            // auth-box 의 첫번재 자식 요소
            const authBoxEl = document.querySelector(".auth-box").children[0];

            // 현재 요소에서 부모 요소 바로 조회해서 자식 요소(본인) 삭제
            authBoxEl.parentElement.removeChild(authBoxEl);

        }, (err) => {
            // 인증 실패시 콜백

           /**
            * front.member.join.html 19열 auth-box 의 자식 요소인 error
            * <div class="message dn"></div>
            * innerText 로 Error message Text 추가 예정
            */

            el.innerText = err.message;
        });
     });
     /* 인증 코드 확인 처리 E */
});

/**
* 주소 검색 후 후속 처리 (회원 가입 양식쪽에서만)
*
*/

function callbackAddressSearch(data) {

    if (!data) {

        return;
    }

    const { zipCode, address } = data;

    frmJoin.zipCode.value = zipCode;
    frmJoin.address.value = address;
}