window.addEventListener("DOMContentLoaded", function () {

    /* 메인 배너 S */

    // Event 바인딩시에는 변수명 필요
    // var swiper = new Swiper(".main-banner .banners", {

    new Swiper(".main-banner .banners", {
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
        },
    });
    /* 메인 배너 S */
});