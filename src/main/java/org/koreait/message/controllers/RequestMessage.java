package org.koreait.message.controllers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.koreait.file.entities.FileInfo;

import java.util.List;

@Data
public class RequestMessage {

    private boolean notice;

    /**
     * 수신 쪽 이메일
     *
     * 필수가 되는 조건 : 회원 -> 회원일 경우
     * 필수가 아닌 조건 : 관리자 -> 전체 공지(notice)일 경우
     *
     */
    @Email
    private String email;

    @NotBlank
    private String gid;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    private List<FileInfo> editorImages;

    private List<FileInfo> attachFiles;
}
