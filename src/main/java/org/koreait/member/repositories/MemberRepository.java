package org.koreait.member.repositories;

import org.koreait.member.entities.Member;
import org.koreait.member.entities.QMember;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

/**
 * 회원 Repository
 *
 */
public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member> {

    // Query Method
    // 권한(Authority)은 @OneToMany라서 지연 로딩 상태지만
    // finByEmail 메서드 사용시에는 즉시 로딩되도록
    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findByEmail(String email);

    // Default 메서드 - Email 중복 체크
    default boolean exists(String email) {

        QMember member = QMember.member;

        return exists(member.email.eq(email));
    }
}