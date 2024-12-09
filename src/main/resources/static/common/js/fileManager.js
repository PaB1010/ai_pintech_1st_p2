// ?? = nullish, undefined 일때 대체
var commonLib = commonLib ?? {};

commonLib.fileManager = {

    /**
    * File Upload 처리
    *
    */
    upload(files, gid, location, single, imageOnly) {

        try {

            /* 유효성 검사 S */

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

            if (!gid || !('' + gid).trim()) {

                throw new Error("잘못된 접근입니다.");
            }

            /* 유효성 검사 E */

            /* 전송 양식 만들기 S */

            const formData = new FormData();

            // 필수 사항
            formData.append("gid", gid);
            formData.append("single",single);
            formData.append("imageOnly", imageOnly);

            // 선택 사항 location 존재시
            if (location) {

                formData.append("location", location);
            }

            for (const file of files) {

                formData.append("file", file);
            }

            /* 전송 양식 만들기 E */

            /* 양식 전송 처리 S */

            const { ajaxLoad } = commonLib;

            // 열린 기능, 콜백 함수 사용
            ajaxLoad("/api/file/upload", function(items) {

                if (typeof callbackFileUpload === 'function') {

                    callbackFileUpload(items);
                }
            }, 'POST', formData);

            /* 양식 전송 처리 E */

        } catch (err) {

            alert(err.message);
            console.error(err);
        }
    }
};

window.addEventListener("DOMContentLoaded", function() {

    const fileUploads = document.getElementsByClassName("file-upload");

    // 연산 후 초기화 할거라서 null
    let fileEl = null;

    for (const el of fileUploads) {

        // 디자인 패턴 - 옵저버 패턴
        el.addEventListener("click", function() {

            // 비구조 할당
            const {gid, location, single, imageOnly} = this.dataset;

            // null인 fileEl에 동적 추가
            if (!fileEl)  {
                fileEl = document.createElement("input");
                fileEl.type = 'file';
            }

            fileEl.gid = gid;
            fileEl.location = location;
            fileEl.imageOnly = imageOnly === 'true';
            fileEl.single = single === 'true';

            // false = 단일 파일 선택
            // true = 여러 파일 선택 가능
            fileEl.multiple = !fileEl.single;

            fileEl.click();

            // File 선택시 - change Event 발생
            // 선택된 객체 e.currentTarget & e.target 의 차이 짚고가기!
            // Event 선 제거 후, 다시 추가 -> Event가 한번만 실행되게 하기 위해서
            fileEl.removeEventListener("change", fileEventHandler);
            fileEl.addEventListener("change", fileEventHandler);

            function fileEventHandler(e) {

                    const files = e.currentTarget.files;

                    const {gid, location, single, imageOnly} = fileEl;

                    // 비구조 할당
                    const { fileManager } = commonLib;

                    fileManager.upload(files, gid, location, single, imageOnly);
                }
        });
    }
});