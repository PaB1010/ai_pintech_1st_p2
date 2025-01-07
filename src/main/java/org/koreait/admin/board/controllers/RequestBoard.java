package org.koreait.admin.board.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.koreait.member.constants.Authority;

@Data
public class RequestBoard {

    // 양식 구분용
    private String mode;

    // 게시판 아이디
    @NotBlank
    private String bid;

    // 게시판명
    @NotBlank
    private String name;

    // 게시판 공개 여부
    // open = 예약어 가능성
    private boolean open;
    
    // 줄개행 문자로 여러 분류 등록
    private String category;

    // 1페이지당 게시글 개수
    private int rowsPerPage;

    // Front View 일때 노출되는 페이지 링크 개수
    private int pageRanges;

    // Mobile View 일때 노출되는 페이지 링크 개수
    private int pageRangesMobile;

    // Editor 사용 여부, default = false;
    private boolean useEditor;

    // Editor 첨부 이미지 여부
    private boolean useEditorImage;

    // 다운로드용 첨부 파일 사용 여부
    private boolean useAttachFile;

    // 댓글 사용 여부
    private boolean useComment;

    // 게시판 스킨
    private String skin;

    /**
     * ALL = 비회원 + 회원 + 관리자
     * USER = 회원 + 관리자
     * ADMIN = 관리자
     */

    // 게시글 목록 접근 권한
    private Authority listAuthority;

    // 게시글 조회 접근 권한
    private Authority viewAuthority;

    // 글쓰기 & 수정 & 삭제 권한 (CUD)
    private Authority writeAuthority;
    
    // 댓글 작성 권한
    private Authority commentAuthority;
}
