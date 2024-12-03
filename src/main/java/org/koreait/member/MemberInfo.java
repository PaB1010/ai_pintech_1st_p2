package org.koreait.member;

import lombok.Builder;
import lombok.ToString;
import org.koreait.member.entities.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserDetails Interface - DTO
 *
 * 구현체
 *
 */
@Builder
@ToString
public class MemberInfo implements UserDetails {

    private String email;

    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private Member member;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // 인가 기능(페이지 접근 제한)
        return authorities;
    }

    @Override
    public String getPassword() {

        return password;
    }

    @Override
    public String getUsername() {

        return email;
    }

    // Account가 만료 여부
     @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    // Account가 잠김 여부
    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    // 비밀번호 만료 여부
    // EX) 일정 기간 지나면 비밀번호 변경 팝업
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    // False시 탈퇴한 회원
    @Override
    public boolean isEnabled() {
        return true;
    }
}