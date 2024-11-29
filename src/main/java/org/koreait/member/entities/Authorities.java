package org.koreait.member.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.member.constants.Authority;

/**
 * 회원 (Member) 다중 권한
 * @ManyToOne
 *
 * 한 회원에 여러 권한 중복 불가
 * EX) user1 - ADMIN, user1 - ADMIN = X
 * 즉 user와 권한을 묶어 복합키로 설정
 *
 */
@Data
@Entity
@IdClass(AuthoritiesId.class) // AuthritiesId = 복합키
public class Authorities {

    @Id
    @ManyToOne(fetch = FetchType.LAZY) // 거의 항상 Join 할 것 같긴 하지만 우선 학습한 대로 LAZY
    private Member member;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Authority authority;
}