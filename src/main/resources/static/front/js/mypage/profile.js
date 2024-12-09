// Mypage 에서 쓰이는 JS
// 올린 사진을 바로 보여주기도 하고, 삭제기능 추가

window.addEventListener("DOMContentLoaded", function() {
// profile IMG 더블 클릭시 삭제 처리

    const el = document.querySelector(".profile-image");

    el.addEventListener("dblclick", function() {

        const seq = this.dataset.seq;

        if (!confirm('정말 삭제하겠습니까?')) {

            return;
        }

        const { fileManager } = commonLib;

        fileManager.delete(seq, function(file) {

            // 삭제 후 후속 처리
            delete el.dataset.seq;
            el.innerHTML = "";
        });
    });
});

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

        el.dataset.seq = file.seq;

        el.innerHTML = `<img src='${file.thumbUrl}&width=250&height=350'>`;
    }
}