package org.koreait.message.controllers;

import lombok.Data;
import org.koreait.global.paging.CommonSearch;

import java.util.List;

@Data
public class MessageSearch extends CommonSearch {

    // 발신 쪽 이메일로 일괄 조회
    private List<String> sender;

    /**
     * mode 값, null 일 경우 send 삼항 예정
     * receiver : 수신 쪽지
     * send : 발신 쪽지
     */
    private String mode;
}