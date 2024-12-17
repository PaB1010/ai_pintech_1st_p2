window.addEventListener("DOMContentLoaded", function() {

    const sendButton = document.getElementById("send-auth-code");

    const authCodeEl = document.getElementById("auth-code");

    const verifyButton = document.getElementById("verify-auth-code");

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



    // 인증 코드 전송 버튼 처리
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
        let timeStr = "00:00";

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

        // Query Selector 사용
        // auth-box class 내의 timer class
        const timerEl = document.querySelector(".auth-box .timer");

        if (timerEl) {

            // 주기적으로 남은 시간 변경
            timerEl.innerHTML = timeStr;
        }
     }
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