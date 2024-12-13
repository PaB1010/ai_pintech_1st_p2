package org.koreait.member.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.constants.Gender;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 회원 (Member) Entity
 *
 */
@Data
@Entity
public class Member extends BaseEntity implements Serializable {

    @Id @GeneratedValue
    private Long seq; // 회원 번호

    @Column(length = 65, nullable = false, unique = true)
    private String email; // 이메일 (로그인 ID)

    @Column(length = 65, nullable = false)
    private String password;

    @Column(length = 40, nullable = false)
    private String name;

    @Column(length = 40, nullable = false)
    private String nickName;

    @Column(nullable = false)
    private LocalDate birthDt; // 생년월일

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    @Column(length = 10, nullable = false)
    private String zipCode;

    @Column(length = 100, nullable = false)
    private String address;

    @Column(length = 100)
    private String addressSub;

    private boolean requiredTerms1; // 필수 약관

    private boolean requiredTerms2;

    private boolean requiredTerms3;

    @Column(length = 50)
    private String optionalTerms; // 선택 약관

    @ToString.Exclude
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE)
    // 관계의 주인은 Many 쪽인 Authorities_member
    private List<Authorities> authorities; // 회원쪽에서도 권한 조회 가능하도록

    // 비밀번호 변경 일시
    private LocalDateTime credentialChangedAt;

    @Transient
    private FileInfo profileImage;
}