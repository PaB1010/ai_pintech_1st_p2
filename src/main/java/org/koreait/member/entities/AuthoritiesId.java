package org.koreait.member.entities;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.koreait.member.constants.Authority;

/**
 * ID Class
 *
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor // ID Class 에는 기본 생성자 필수
public class AuthoritiesId {

    private Member member;

    private Authority authority;
}