package org.koreait.message.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;
import org.koreait.message.constants.MessageStatus;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
// columnList 에서는 DB Table 명이 아닌 Entity 의 Field 명 사용
@Table(indexes = @Index(name = "idx_notice_created_at", columnList = "notice DESC, createdAt DESC"))
public class Message extends BaseEntity {

    @Id
    @GeneratedValue
    private Long seq;

    // 공지
    private boolean notice;

    // File Upload 경우 사용할 Group ID
    @Column(length = 45, nullable = false)
    private String gid;

    // 읽음 여부
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private MessageStatus status;


    // 보내는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sender")
    private Member sender;

    // 받는 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="receiver")
    private Member receiver;

    // 제목
    @Column(length = 150, nullable = false)
    private String subject;

    // 내용
    @Lob
    @Column(nullable = false)
    private String content;

    // 2차 가공용(추가정보 형태) content 쪽 editor 에서 사용할 이미지
    @Transient
    private List<FileInfo> editorImages;

    // 2차 가공용(추가정보 형태) 다운로드 받을 첨부 파일
    @Transient
    private List<FileInfo> attachFiles;

    // 2차 가공용(추가정보 형태) 수신자 = 현재 로그인 계정 일치 여부
    @Transient
    private boolean received;
    
    // 2차 가공용(추가정보 형태) 삭제 가능 여부 - 일반 사용자는 공지 쪽지 삭제 불가
    @Transient
    private boolean deletable;

    // 발신자가 쪽지 삭제
    private boolean deletedBySender;

    // 수신자가 쪽지 삭제
    private boolean deletedByReceiver;
}