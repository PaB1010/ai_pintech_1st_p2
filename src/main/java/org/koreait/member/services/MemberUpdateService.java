package org.koreait.member.services;

import org.koreait.member.controllers.RequestJoin;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

// @Lazy = 지연 로딩 - 최초로 해당 Bean을 사용할 때 생성
@Lazy
@Service
public class MemberUpdateService {


    /**
     * 메서드 오버로드 - 커맨드 객체의 타입에 따라서
     * RequestJoin이면 회원 가입 처리
     * RequestProfile이면 회원 정보 수정 처리
     *
     * @param form
     */
    public void process(RequestJoin form) {

    }

    /**
     * 회원 정보 추가 OR 수정 완료 처리
     *
     */
    public void save() {

    }
}