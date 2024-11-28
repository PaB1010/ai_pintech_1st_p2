package org.koreait.pokemon.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

/**
 * DB..?
 *
 */
@Data
@Entity
public class Pokemon {

    // wrapper class 사용
    @Id
    private Long seq;

    @Column(length=50)
    // 포켓몬 이름 기본값(한글)
    private String name;

    // 포켓몬 이름(영어)
    @Column(length=50)
    private String nameEn;

    private int weight;

    private int height;

    private int baseExperience;

    /*
    기본 데이터형일땐 not null 이 붙음 래퍼 클래스 형태의 Integer Long 을 사용시엔 not null 이 안붙음 필수가 아닌 경우 기본형이 아닌 래퍼 클래스로 정의하면 됨.
     */

    private String frontImage;

    // 포켓몬 설명 Text
    @Lob
    private String flavorText;

    // Type1 || Type2 || Type3
    private String types;

    // 특성1 || 특성2 || 특성3
    private String abilities;
}