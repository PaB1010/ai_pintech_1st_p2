package org.koreait.pokemon.api.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * {"count":1302,
 *  * "next":"https://pokeapi.co/api/v2/pokemon?offset=20&limit=20"
 *  * "previous":null,"
 *  * results":[{"name":"bulbasaur","url":"https://pokeapi.co/api/v2/pokemon/1/"}
 *
 * 여기서 results를 담당할 Data Class
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UrlItem {

    private String name;
    private String url;
}