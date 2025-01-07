package org.koreait.member.constants;

/**
 * 회원 권한 상수
 *
 */
public enum Authority {

    ALL, // 모든 사용자 (비회원 포함)
    USER, // 일반 회원
    MANAGER, // 부관리자
    ADMIN // 최고 관리자
    /*
    추후 게시판 만들떄 추가 예정
    예상) 비 로그인 상태?
     */
}
