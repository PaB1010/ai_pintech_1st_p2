window.addEventListener("DOMContentLoaded", function () {

    // CKEditor 공통 기능 비구조 할당
    const { loadEditor } = commonLib;

    loadEditor("content", 350)
        .then((editor) => {
            
            // 전역 변수로 등록
            // then 구간 외부에서도 접근 가능하게 처리
            window.editor = editor;
        });

    // IMG 본문 추가 이벤트 처리
    const insertEditors = document.querySelectorAll(".insert-editor");

    insertEditors.forEach(el => {

        el.addEventListener("click", e => commonLib.insertEditorImage(e.currentTarget.dataset.url));
    });

    // 첨부 파일 삭제 버튼 이벤트 처리
    const removeEls = document.querySelectorAll(".file-item .remove");

    const { fileManager } = commonLib;

    removeEls.forEach(el => {

        el.addEventListener("click", e => {

           if (confirm('정말 삭제하겠습니까?')) {

               console.log(el);
               const seq = e.currentTarget.dataset.seq;

               fileManager.delete(seq, () => {

                   const el = document.getElementById(`file-${seq}`);

                   el.parentElement.removeChild(el);
               });
           }
        });
    });
});

/**
 * File Upload 완료 후 성공 후속 처리 콜백 함수
 *
 * @param files
 */
function callbackFileUpload(files) {

    // files 없을 경우 처리 X
    if (!files || files.length === 0) return;

    const imageUrls = [];

    // 추가될 부분이 서로의 분기
    const targetEditor = document.getElementById("editor-files");
    const targetAttach = document.getElementById("attach-files");
    
    const tpl = document.getElementById("tpl-file-item").innerHTML;

    const domParser = new DOMParser();

    const { fileManager } = commonLib;

    for (const { seq, fileUrl, fileName, location } of files) {

        // 파일 목록 -> tpl 치환

        let html = tpl;
        
        // g = 전역
        html = html.replace(/\[seq\]/g, seq)
            .replace(/\[fileName\]/g, fileName)
            .replace(/\[fileUrl\]/g, fileUrl);
        
        // DOM 객체 생성
        const dom = domParser.parseFromString(html, "text/html");

        const fileItem = dom.querySelector(".file-item");

        const el = fileItem.querySelector(".insert-editor");

        const removeEl = fileItem.querySelector(".remove");

        if (location === 'editor') {
            // Editor 에 추가될 IMG 일 경우

            imageUrls.push(fileUrl);

            targetEditor.append(fileItem);

            el.addEventListener("click", function () {

                const { url } = this.dataset;

                commonLib.insertEditorImage(url);
            });
            
        } else {
            // 다운로드를 위한 첨부 파일일 경우
            
            // el 자기 자신 제거
            el.parentElement.removeChild(el);

            targetAttach.append(fileItem);
        }
        
        removeEl.addEventListener("click", function () {

            if (!confirm('정말 삭제하겠습니까?')) return;

            fileManager.delete(seq, (f) => {
               // 삭제 후 후속 처리
               
               const el = document.getElementById(`file-${f.seq}`);
               
               // 혹시 모르니 el 자기자신 요소있을 경우 삭제
               if (el) el.parentElement.removeChild(el);
            });
        });
    }

    // IMG 한번에 모아서 Insert
    if (imageUrls.length > 0) commonLib.insertEditorImage(imageUrls);
}