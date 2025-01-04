// commonLib 객체 없을 경우 새로 생성
var commonLib = commonLib ?? {};

commonLib.follow = {

    /**
     * Follow 기능
     *
     * @param seq
     * @param callbackFollow
     */
    follow(seq, callbackFollow) {

        const { ajaxLoad } = commonLib;

        // ajaxLoad(`api/member/follow/${seq}`, function(item) {
        //
        //     if (typeof callbackFollow === 'function') {
        //
        //         callbackFollow(item);
        //     }
        // }, "GET")
        //     .catch(err => console.error(err));

        ajaxLoad(`api/member/follow/${seq}`, null, "GET")
            .then(res => {
                if (typeof callbackFollow === 'function') callbackFollow();
            })
            .catch(err => console.error(err));
  },

    /**
     * Unfollow 기능
     *
     * @param seq
     * @param callbackUnfollow
     */
    unfollow(seq, callbackUnfollow) {

        const { ajaxLoad } = commonLib;

        ajaxLoad(`api/member/unfollow/${seq}`, function(item) {

            if (typeof callbackUnfollow === 'function') {

                callbackUnfollow(item);
            }
        }, "GET")
            // .catch(err => console.error(err));
    }
};

window.addEventListener("DOMContentLoaded", function () {

    const followings = document.getElementsByClassName("follow_action");

    const { follow, unfollow } = commonLib.follow;

    // Follow & UnFollow 처리
    for (const el of followings) {

        el.addEventListener("click", function() {

           const classList = this.classList;

           const action = classList.contains("unfollow") ? unfollow : follow;

           action(this.dataset.seq, function() {

               if (classList.contains("unfollow")) {

                   classList.remove("unfollow");

                   el.innerText = "Follow";

               } else {

                   classList.add("unfollow");

                   el.innerText = "Unfollow";
               }
           })
        });
    }
});