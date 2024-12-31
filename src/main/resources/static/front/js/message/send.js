window.addEventListener("DOMContentLoaded", function () {

    // CKEditor 공통 기능 비구조 할당
    const { loadEditor } = commonLib;

    loadEditor("content", 350)
        .then((editor) => {
            
            // 전역 변수로 등록
            // then 구간 외부에서도 접근 가능하게 처리
            window.editor = editor;
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

    for (const { seq, fileUrl, fileName, location } of files) {

        // 파일 목록 -> tpl 치환

        let html = tpl;
        
        // g = 전역
        html = html.replace(/\[seq\]/g, seq)
            .replace(/\[fileName\]/g, fileName)
            .replace(/\[fileUrl\]/g, fileUrl);
        
        // DOM 객체 생성
        const dom = domParser.parseFromString(html, "text/html");

        const fileItem = dom.querySelector(".file-item")

        if (location === 'editor') {
            // Editor 에 추가될 IMG 일 경우

            imageUrls.push(fileUrl);

            targetEditor.append(fileItem);
            
        } else {
            // 다운로드를 위한 첨부 파일일 경우

            const el = fileItem.querySelector(".insert-editor");
            
            // el 자기 자신 제거
            el.parentElement.removeChild(el);

            targetAttach.append(fileItem);
        }
    }

    // IMG 한번에 모아서 Insert
    if (imageUrls.length > 0) insertImage(imageUrls);
}

/**
 * Editor 에 올라갈 IMG
 */
function insertImage(imageUrls) {

    // execute = 이미 정해져있는 명령어
    // Editor 에 IMG Upload
    editor.execute('insertImage', { source : imageUrls })

}