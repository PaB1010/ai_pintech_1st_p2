window.addEventListener("DOMContentLoaded", function () {

    /* 메인 배너 S */

    // Event 바인딩시에는 변수명 필요
    // var swiper = new Swiper(".main-banner .banners", {

    new Swiper(".main-banner .banners", {
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
        },

        loop: true, // 무한 루프

        autoplay: {
            delay: 1000, // 1초
        },

        speed: 1000, // 1초
    });
    /* 메인 배너 S */
});