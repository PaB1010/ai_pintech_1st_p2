package org.koreait.pokemon.api.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 포켓몬 속성
 *
 *  "genera": [
 *         {
 *             "genus": "씨앗포켓몬",
 *             "language": {
 *                 "name": "ko",
 *                 "url": "https://pokeapi.co/api/v2/language/3/"
 *             }
 *         }
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Genus {

    private String genus;

    private UrlItem language;
}