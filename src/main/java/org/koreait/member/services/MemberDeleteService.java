package org.koreait.member.services;

import lombok.RequiredArgsConstructor;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Member 삭제 기능(Service)
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class MemberDeleteService {

    private final MemberRepository memberRepository;

    private final MemberInfoService infoService;

    public Member delete(String email) {

        // Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

//        Member member = infoService.loadUserByUsername(email).getUsername();
//
//        memberRepository.delete(member);
//        memberRepository.flush();
//
//        return member;
    }
}