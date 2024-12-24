package org.koreait.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 설정 항목 기본 값
 * JSON 형태 - Key & Value 쌍 Map 형태로 저장
 *
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CodeValue {

    @Id
    @Column(name = "_CODE", length = 45)
    private String code;

    @Lob
    @Column(name = "_VALUE")
    private String value;
}