package org.koreait.pokemon.api.entities;

import lombok.Data;

import java.util.List;

/**
 * JSON 형태로 Api 응답 받을 DataClass
 *
 * {"count":1302,
 * "next":"https://pokeapi.co/api/v2/pokemon?offset=20&limit=20"
 * "previous":null,"
 * results":[{"name":"bulbasaur","url":"https://pokeapi.co/api/v2/pokemon/1/"}
 *
 */
@Data
public class ApiResponse {

    //
    private int count;

    // 다음 주소
    private String next;

    private String previous;

    private List<UrlItem> results;
}