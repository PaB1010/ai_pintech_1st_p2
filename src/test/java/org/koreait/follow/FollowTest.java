package org.koreait.follow;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.koreait.follow.repositories.FollowRepository;
import org.koreait.follow.services.FollowService;
import org.koreait.global.paging.CommonSearch;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles({"default", "test"})
public class FollowTest {

    @Autowired
    private FollowRepository followRepository;

    private FollowService followService;

    private MemberRepository memberRepository;

    private HttpServletRequest request;

    private HttpSession session;

    private Member member1;
    private Member member2;
    private Member member3;

    private CommonSearch paging;
}