// ?? = nullish, undefined일때 대체
var commonLib = commonLib ?? {};

commonLib.fileManager = {

    /**
    * File Upload 처리
    *
    */
    upload(files, gid, location, single, imageOnly) {

        try {

            if (!files || files.length === 0) {

                throw new Error("파일을 선택하세요.");
            }

            // Image 만 Upload 하는 경우 검증
            if (imageOnly) {

                for (const file of files) {

                    // Image 가 아닌 File 인 경우
                    if (file.type.indexOf("image/") === -1) {

                        throw new Error("이미지 형식이 아닙니다.");
                    }
                }
            }

        } catch (err) {

            alert(err.message);
            console.error(err);
        }
    }
};

window.addEventListener("DOMContentLoaded", function() {

    const fileUploads = document.getElementsByClassName("file-upload");

    const fileEl = document.createElement("input");

    fileEl.type = 'file';

    for (const el of fileUploads) {

        // 디자인 패턴 - 옵저버 패턴
        el.addEventListener("click", function() {

            // 비구조 할당
            const {gid, location, single, imageOnly} = this.dataset;

            fileEl.gid = gid;
            fileEl.location = location;
            fileEl.imageOnly = imageOnly === 'true';
            fileEl.single = single === 'true';

            // false = 단일 파일 선택
            // true = 여러 파일 선택 가능
            fileEl.multiple = !fileEl.single;

            fileEl.click();
        });
    }

    // File 선택시 - change Event 발생
    // 선택된 객체 e.currentTarget & e.target 의 차이
    fileEl.addEventListener("change", function(e) {

        const files = e.currentTarget.files;

        const {gid, location, single, imageOnly} = fileEl;


        // 비구조 할당
        const { fileManager } = commonLib;

        fileManager.upload(files, gid, location, single, imageOnly);
    });
});