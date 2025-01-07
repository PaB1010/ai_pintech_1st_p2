// WebSocket Message 관련, 모든 Page 추가 필수 (Admin 제외한 Front, Mobile)

const webSocket = new WebSocket(`ws://${location.host}/msg`);

webSocket.addEventListener("message", function(data) {

    // 현재 로그인한 유저를 user Meta 태그 통해 가져옴
    const email = commonLib.getMeta("user");

    // 비구조 할당 통해 분해
    const { item, totalUnRead } = JSON.parse(data.data);

    /* 공지사항 || 현재 로그인한 유저 = 수신자일 경우 쪽지 알람 기능 */
    let isShow = false;

    // 공지사항
    // 없을 경우 undefined 하도록 ?. 옵셔널 체이닝 사용
    if (item.notice || (email && email === item?.receiver?.email)) isShow = true;

    // 메세지 팝업
    if (isShow) commonLib.message("쪽지가 왔습니다.");

    // console.log(totalUnRead, JSON.parse(data.data));

    // 미열람 쪽지가 1개 이상일 경우 dn 클래스 제거해 totalUnRead 노출
    if (totalUnRead > 0) {

        const badge = document.querySelector(".link-mypage .badge");

        if (badge) {

            badge.innerText = totalUnRead;

            badge.classList.remove("dn");
        }
    }
});