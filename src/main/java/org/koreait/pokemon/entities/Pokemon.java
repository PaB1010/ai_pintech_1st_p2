package org.koreait.pokemon.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;
import org.koreait.global.entities.BaseEntity;

/**
 * DB..?
 *
 */
@Data
@Entity
public class Pokemon extends BaseEntity {

    // wrapper class 사용
    @Id
    private Long seq;

    // 포켓몬 이름 기본값(한글)
    @Column(length = 50)
    private String name;

    // 포켓몬 이름(영어)
    @Column(length = 50)
    private String nameEn;

    private int weight;

    private int height;

    private int baseExperience;

    /*
    기본 자료형일땐 not null 붙음
    Wrapper Class는 Null 허용되므로 Integer, Long 등 사용시 not null 안붙음
    즉 필수가 아닌 경우 기본 자료형이 아닌 Wrapper Class 로 정의
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