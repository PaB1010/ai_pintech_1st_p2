// ?? = nullish, undefined 일때 대체
var commonLib = commonLib ?? {};

commonLib.fileManager = {

    /**
    * File Upload 처리
    *
    */
    upload(files, gid, location, single, imageOnly, done) {

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

            /* 동적 전송 양식 만들기 S */

            const formData = new FormData();

            // 필수 사항
            formData.append("gid", gid);
            formData.append("single",single);
            formData.append("imageOnly", imageOnly);
            formData.append("done", done);

            // 선택 사항 location 존재시
            if (location) {

                formData.append("location", location);
            }

            for (const file of files) {

                formData.append("file", file);
            }

            /* 동적 전송 양식 만들기 E */

            /* 양식 전송 처리 S */

            const { ajaxLoad } = commonLib;

            // 열린 기능, 콜백 함수 사용
            ajaxLoad("/api/file/upload", function(items) {

                if (typeof callbackFileUpload === 'function') {

                    callbackFileUpload(items);
                }
            }, 'POST', formData);

            // 전송 성공/실패 여부 상관 없이 Data 비워주기
            window.fileEl = null;


            /* 양식 전송 처리 E */

        } catch (err) {

            alert(err.message);
            console.error(err);
        }
    },
    /**
    *  File 등록번호(SEQ)로 File 삭제
    *  @param seq : File 등록 번호
    *  @param callback : 삭제 후 후속 처리 callback 함수
    */
    delete(seq, callback) {

        const { ajaxLoad } = commonLib;

        ajaxLoad(`/api/file/delete/${seq}`, file => callback(file), 'DELETE');
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
            const {gid, location, single, imageOnly, done } = this.dataset;

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

            // Upload 완료 하자마자 완료 처리
            fileEl.done = done === 'true';

            fileEl.click();

            // File 선택시 - change Event 발생
            // 선택된 객체 e.currentTarget & e.target 의 차이 짚고가기!
            // Event 선 제거 후, 다시 추가 -> Event 가 한번만 실행되게 하기 위해서
            fileEl.removeEventListener("change", fileEventHandler);
            fileEl.addEventListener("change", fileEventHandler);
        });
        function fileEventHandler(e) {

            const files = e.currentTarget.files;

            const {gid, location, single, imageOnly, done } = fileEl;

            // 비구조 할당
            const { fileManager } = commonLib;

            fileManager.upload(files, gid, location, single, imageOnly, done);
        }
    }

    // Drag & Drop File Upload 처리
    const dragUploads = document.getElementsByClassName("drag-upload");

    for (const el of dragUploads) {

        el.addEventListener("dragover", function(e) {
            // 기본 동작 차단 (File 내용 새 탭으로 Open)
            e.preventDefault();
        });

        el.addEventListener("drop", function(e) {
            // 기본 동작 차단
            e.preventDefault();

            const files = e.dataTransfer.files;

            let {gid, location, single, imageOnly, done} = this.dataset;

            single = single === "true";
            imageOnly = imageOnly === "true";
            done = done === "true";

            // 단일 File Upload 이지만 여러개를 선택한 경우
            if (single && files.length > 1 ) {

                alret("하나의 파일만 업로드하세요.");
                return;
            }

            const { fileManager } = commonLib;

            fileManager.upload(files, gid, location, single, imageOnly, done);
        });
    }
});