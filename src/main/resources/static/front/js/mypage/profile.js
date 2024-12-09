// Mypage 에서 쓰이는 JS
// 올린 사진을 바로 보여주기도 하고, 삭제기능 추가

/**
*  FileUpload 후속 처리 (콜백 함수 이용)
*
*/
function callbackFileUpload(files) {

    if (!files || files.length === 0) {

        return;
    }

    const el = document.querySelector(".profile-image");

    if (el) {

        const file = files[0];

        el.innerHTML = `<img src='${file.fileUrl}'>`;
    }

}