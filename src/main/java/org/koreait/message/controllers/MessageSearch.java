package org.koreait.message.controllers;

import lombok.Data;
import org.koreait.global.paging.CommonSearch;

import java.util.List;

@Data
public class MessageSearch extends CommonSearch {

    // 수신쪽 이메일로 일괄 조회
    private List<String> receiver;
}