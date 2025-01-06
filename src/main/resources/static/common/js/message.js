// WebSocket Message 관련, 모든 Page 추가 필수 (Admin 제외한 Front, Mobile)

const webSocket = new WebSocket(`ws://${location.host}/msg`);

webSocket.addEventListener("message", function(data) {

    // 수신 받은 메세지 확인
    console.log("message : ", data);
});