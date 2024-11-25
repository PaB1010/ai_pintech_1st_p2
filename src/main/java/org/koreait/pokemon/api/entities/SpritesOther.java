package org.koreait.pokemon.api.entities;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

/**
 * Pokemon API 에서
 * Sprites.Other
 * Data Class
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpritesOther {

    // @JsonAlias("별칭")
    // 변수명과 JSON의 실제 이름이 다를 경우 매칭을 위해 사용하는 Annotation
    @JsonAlias("official-artwork")
    private Map<String, String> officialArtwork;
}