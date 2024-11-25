package org.koreait.pokemon.api.entities;

import lombok.Data;

/**
 * DB..?
 *
 */
@Data
public class Pokemon {

    // wrapper class 사용
    private Long seq;

    // 포켓몬 이름 기본값(한글)
    private String name;

    // 포켓몬 이름(영어)
    private String nameEn;

    private int weight;

    private int height;

    private int baseExperience;

    private String frontImage;

    // 포켓몬 설명 Text
    private String flavorText;

    // Type1 || Type2 || Type3
    private String types;

    // 특성1 || 특성2 || 특성3
    private String abilities;
}