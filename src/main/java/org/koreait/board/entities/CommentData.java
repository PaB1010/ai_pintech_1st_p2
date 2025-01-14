package org.koreait.board.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;

@Data
@Entity
public class CommentData extends BaseEntity {

    @Id @GeneratedValue
    private Long seq;

    // 회원 한명에 여러 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    // 한개의 게시글에 여러 댓글
    @ManyToOne(fetch = FetchType.LAZY)
    private BoardData data;

    // 작성자
    @Column(length = 40, nullable = false)
    private String commenter;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(length = 20)
    private String ipAddr;

    @Column(length = 150)
    private String userAgent;
}