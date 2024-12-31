package org.koreait.message.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseEntity;
import org.koreait.member.entities.Member;
import org.koreait.message.constants.MessageStatus;

import java.util.List;

@Data
@Entity
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
}