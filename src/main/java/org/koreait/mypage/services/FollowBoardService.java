package org.koreait.mypage.services;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Following & Follower 회원 게시글 목록 조회
 */
@Lazy
@Service
@RequiredArgsConstructor
public class FollowBoardService {

    private final MemberUtil memberUtil;

    private final FollowService followService;

    private final HttpServletRequest request;

    private final EntityManager em;
}