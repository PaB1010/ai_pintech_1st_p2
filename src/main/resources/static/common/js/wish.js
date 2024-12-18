window.addEventListener("DOMContentLoaded", function() {

    const wishButtons = document.getElementsByClassName("wish-btn");

    // iterator - For of 문 가능
    for (const el of wishButtons) {

        el.addEventListener("click", function() {
            /**
             * 1. 로그인 상태 체크 - class 에 guest 가 포함되어 있으면 미로그인 상태
             * 2. 미로그인 상태 -> 로그인 페이지 주소 이동, 로그인 완료시에는 현재 페이지로 다시 이동
             * redirectUrl : location 객체의 pathname + search 다시 돌아올 현재 페이지 기록
             */

            if (this.classList.contains("guest")) { // 미로그인 상태

                alert("로그인이 필요한 서비스 입니다.");

                // 비구조 할당
                const { pathname, search } = location;

                // search 는 항상 있는 것은 아니라 삼항조건으로 유무 체크 후 추가
                const redirectUrl = search ? pathname + search : pathname;

                // 로그인 후 원래 페이지로 돌아오도록
                location.href = commonLib.url(`/member/login?redirectUrl=${redirectUrl}`);

                return;
            }
        });
    }
});