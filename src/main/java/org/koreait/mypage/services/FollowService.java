package org.koreait.mypage.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.global.paging.CommonSearch;
import org.koreait.global.paging.ListData;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.member.services.MemberInfoService;
import org.koreait.mypage.entities.Follow;
import org.koreait.mypage.entities.QFollow;
import org.koreait.mypage.repositories.FollowRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Follow Service
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    private final MemberRepository memberRepository;

    private final MemberInfoService memberInfoService;

    private final MemberUtil utils;

    private final HttpServletRequest request;


    /**
     * Follow 기능
     *
     * @param follower : Following 할 회원
     */
    public void follow(Member follower) {

        // 회원 전용 기능이므로 비로그인시 처리 X
        if (!utils.isLogin()) {
            return;
        }

        try {
            Member following = utils.getMember();


            // 자기 자신은 팔로우 하지 못하도록 체크
           // if (follower.getSeq().equals(following.getSeq())) return;

            Follow follow = Follow.builder()
                    .following(following)
                    .follower(follower)
                    .build();

            followRepository.saveAndFlush(follow);

            // follow Data 중복시 유니크 제약 조건 예외 발생
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // Seq 로 Follow
    public void follow(Long seq) {

        Member follower = memberRepository.findById(seq).orElse(null);

        if (follower == null) return;

        follow(follower);
    }

    // Email 로 Follow
    public void follow(String email) {

        Member follower = memberRepository.findByEmail(email).orElse(null);

        if (follower == null) return;

        follow(follower);
    }

    /**
     * UnFollow 기능
     *
     * @param follower : Following 취소할 회원
     */
    public void unfollow(Member follower) {

        // follow 와 마찬가지로 회원 전용 기능이므로 비로그인시 처리 X
        if (utils.isLogin()) return;

        if (follower == null) return;

        Member following = utils.getMember();

        Follow follow = followRepository.findByFollowerAndFollower(following, follower);

        followRepository.delete(follow);
        followRepository.flush();
    }

    // Seq 로 unFollow
    public void unfollow(Long seq) {

        Member follower = memberRepository.findById(seq).orElse(null);

        if (follower == null) return;
    }

    // Email 로 unFollow
    public void unfollow(String email) {

        Member follower = memberRepository.findByEmail(email).orElse(null);

        if (follower == null) return;
    }

    /**
     * 현재 로그인한 회원을 follow 한 회원 목록 페이지
     *
     * @param paging
     * @return
     */
    public ListData<Member> getFollowers(CommonSearch paging) {

        if (!utils.isLogin()) return null;

        return followRepository.getFollowers(utils.getMember(), paging, request);
    }

    /**
     * 현재 로그인한 회원을 follow 한 회원 목록 페이지
     *
     * @param paging
     * @return
     */
    public ListData<Member> getFollowings(CommonSearch paging) {

        if (!utils.isLogin()) return null;

        return followRepository.getFollowings(utils.getMember(), paging, request);
    }

    /**
     * 현재 로그인한 회원을 follow 한 회원 목록
     * @return
     */
    public List<Member> getFollowers() {

        if (!utils.isLogin()) return null;

        return followRepository.getFollowers(utils.getMember());
    }

    /**
     * 현재 로그인한 회원을 follow 한 회원 목록
     * @return
     */
    public List<Member> getFollowings() {

        if (!utils.isLogin()) return null;

        return followRepository.getFollowings(utils.getMember());
    }

    /**
     * 해당 회원을 follow 한 회원 총합
     *
     * @return
     */
    public long getTotalFollowers() {

        if (utils.isLogin()) return followRepository.getTotalFollowers(utils.getMember());

        return 0L;
    }

    /**
     * 해당 회원이 follow 한 회원 총합
     *
     * @return
     */
    public long getTotalFollowings() {

        if (utils.isLogin()) return followRepository.getTotalFollowings(utils.getMember());

        return 0L;
    }

    /**
     * Follow & Following 목록
     *
     * @param mode : follower - 팔로워 회원 목록 / following - 팔로잉 회원 목록
     * @param paging
     * @return
     */
    public ListData<Member> getList(String mode, CommonSearch paging) {

        mode = StringUtils.hasText(mode) ? mode : "follower";

        ListData<Member> data = mode.equals("following") ? getFollowings(paging) : getFollowers(paging);

        System.out.println("서비스 데이터" + data);

        // 추가 정보 처리 (2차 가공)
        data.getItems().forEach(memberInfoService::addInfo);

        return data;
    }

    /**
     * Following 상태 여부 체크
     *
     * @param email
     * @return
     */
    public boolean isFollowing(String email) {

        if (!utils.isLogin()) return false;

        QFollow follow = QFollow.follow;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(follow.follower.email.eq(email))
                .and(follow.following.in(utils.getMember()));

        return followRepository.exists(builder);
    }


}