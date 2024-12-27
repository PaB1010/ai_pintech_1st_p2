package org.koreait.admin.member.controllers;

import lombok.Data;
import org.koreait.global.paging.CommonSearch;
import org.koreait.member.constants.Authority;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * 회원 조회 Data Class (관리자용)
 *
 */
@Data
public class MemberSearch extends CommonSearch {

    private List<String> email;

    private List<Authority> authority;

    // createdAt 일 경우 가입일자 기준으로 sDate / eDate
    // 수정일자일 경우 그 기준으로 sDate / eDate 등등
    // 검색 기준 타입
    private String dateType;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate sDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eDate;
}