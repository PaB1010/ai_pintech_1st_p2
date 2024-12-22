package org.koreait.member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koreait.global.entities.BaseEntity;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Follow extends BaseEntity {

    @Id @GeneratedValue
    private Long seq;

    // 팔로잉 회원
    @ManyToOne(fetch = FetchType.LAZY)
    private Member following;

    // 팔로우 회원
    @ManyToOne(fetch = FetchType.LAZY)
    private Member follower;
}