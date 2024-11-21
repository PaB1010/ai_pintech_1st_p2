window.addEventListener("DOMContentLoaded", function() {

    // checkBox 전체 토글 기능 S
    const checkAlls = document.getElementsByClassName("check-all");

    for (const el of checkAlls) {

        el.addEventListener("click", function() {

            const { targetClass } = this.dataset;

            if (!targetClass) { // 토글할 체크박스의 클래스가 설정되지 않은 경우는 진행 X
                return;
            }

            const chks = document.getElementsByClassName(targetClass);

            for (const chk of chks) {

                chk.checked = this.checked;
            }
        });
    }
    // checkBox 전체 토글 기능 E
});