window.addEventListener("DOMContentLoaded", function() {
    commonLib.loadEditor("description", 350)
        .then(editor => window.editor = editor);

    const { insertEditorImage, fileManager } = commonLib;

    // 에디터 이미지 추가 처리
    // getElementByClassName = HTMLCollection 객체로 Iterator 없어 forEach 불가능
    const insertEditors = document.getElementsByClassName("insert-editor");

    for (const el of insertEditors) {

        // 화살표 함수 : 이때의 this = window
        el.addEventListener("click", (e) => insertEditorImage(e.currentTarget.dataset.url));
    }

    // 파일 삭제 처리
    // querySelector 반환 값= NodeList 로 Iterator 포함
    const removeEls = document.querySelectorAll(".file-item .remove, .image-item .remove");

    for (const el of removeEls) {

        // this 사용할 예정이라 올바른 이벤트 바인딩을 위해 화살표 함수가 아닌 function 사용
        el.addEventListener("click", function() {

            if (!confirm('정말 삭제하겠습니까?')) return;

            const { seq } = this.dataset;

            fileManager.delete(seq, () => {

                if (!confirm('정말 처리하겠습니까?')) return;

                // 삭제 후 후속처리
                const el = document.getElementById(`file-${seq}`);

               el.parentElement.removeChild(el);
            });
        });
    }
});

/**
 * 파일 업로드 후 후속 처리
 *
 * @param files
 */
function callbackFileUpload(files) {

    if (!files || files.length === 0) return;

    const tplFile = document.getElementById("tpl-file-item").innerHTML;
    const tplImage = document.getElementById("tpl-image-item").innerHTML;

    const targetEditor = document.getElementById("editor-images");
    const targetMain = document.getElementById("main-images");
    const targetList = document.getElementById("list-images");

    const { insertEditorImage, fileManager } = commonLib;

    const imageUrls = [];

    const domParser = new DOMParser();

    for (const { seq, location, fileName, fileUrl, thumbUrl } of files) {

        let html = location === 'editor' ? tplFile : tplImage;

        html = html.replace(/\[seq\]/g, seq)
            .replace(/\[fileName\]/g, fileName)
            .replace(/\[fileUrl\]/g, fileUrl)
            .replace(/\[thumbUrl\]/g, `${thumbUrl}&width=200&height=100`);

        const dom = domParser.parseFromString(html, "text/html");

        const el = dom.querySelector(".file-item, .image-item");

        const insertEditor = el.querySelector(".insert-editor");

        if (insertEditor) { // 업로드 이미지 있을 경우 클릭시 에디터에 추가하는 이벤트 추가

            insertEditor.addEventListener("click", () => insertEditorImage(fileUrl));
        }

        const removeEl = el.querySelector(".remove");

        removeEl.addEventListener("click", () => {

            fileManager.delete(seq, () => { // 파일 삭제 후 후속처리

                const el = document.getElementById(`file-${seq}`);

                el.parentElement.removeChild(el);
            });
        });

        switch (location) {

            case "main" : // 메인 이미지

                targetMain.append(el);

                break;

            case "list" : // 목록 이미지

                targetList.append(el);

                break;

            default: // 에디터 이미지

                imageUrls.push(fileUrl);

                targetEditor.append(el);
        }
    }

    if (imageUrls.length > 0) insertEditorImage(imageUrls);
}