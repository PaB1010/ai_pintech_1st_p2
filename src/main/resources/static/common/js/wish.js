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

            // 로그인 상태

            let apiUrl = commonLib.url("/api/wish/");

            const classList = this.classList;

            // class on이 있으면 remove, 없으면 add
            if (classList.contains("on")) { // 찜하기 제거 (remove)

                apiUrl += "remove";

            } else { // 찜하기 (add)

                apiUrl += "add";
            }

            // 비구조 할당으로 dataset 할당하고 seq, type 가져오기
            const { seq, type } = this.dataset;

            // 요청 주소 완성 (토글 형태)
            apiUrl += `?seq=${seq}&type=${type}`;

            const { ajaxLoad } = commonLib;

            const icon = this.querySelector("i");

            (async() => {

                try {
                    // 응답 코드 204
                    await ajaxLoad(apiUrl);

                    if (classList.contains("on")) { // on 제거 처리

                        icon.className = "xi-heart-o"

                    } else { // on 추가 처리

                        icon.className = "xi-heart";

                    }

                    classList.toggle("on");

                } catch (err) {

                    alert(err.message);
                }
            })();
        });
    }
});