package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Pokemon Api
 *
 * 포켓몬 설명 Text
 * FlavorText Data Class
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlavorText {

    @JsonAlias("flavor_text")
    private String flavorText;

    private UrlItem language;

    private UrlItem version;
}