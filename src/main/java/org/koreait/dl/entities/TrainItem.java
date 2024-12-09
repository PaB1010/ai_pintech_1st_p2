package org.koreait.dl.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.koreait.global.entities.BaseEntity;

/**
 * 지도 학슴용 Data
 * 
 * 모든 Data 는 반드시 숫자 - int & double & Long
 *
 * 사용하지 않을 특성은 0으로 처리 예정
 * 
 */
// Entity = 기본 생성자 필수, Builder = 기본 생성자 private
// 즉 @NoArgsConstructor 단독 사용시 무조건 오류
@AllArgsConstructor // Builder 패턴일때 기본 생성자가 public으로 접근가능해야 하는 경우
@NoArgsConstructor // 편법으로 이 2개의 Annotation 같이 사용해 오류 방지
@Entity
@Data
@Builder
public class TrainItem extends BaseEntity {

    @Id @GeneratedValue
    private Long seq;

    private int item1;
    private int item2;
    private int item3;
    private int item4;
    private int item5;
    private int item6;
    private int item7;
    private int item8;
    private int item9;
    private int item10;

    // Target Data
    private int result;
}