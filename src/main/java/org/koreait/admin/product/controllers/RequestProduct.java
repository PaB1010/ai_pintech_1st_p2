package org.koreait.admin.product.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.koreait.product.constants.DiscountType;

/**
 * 상품 커맨드 객체
 */
@Data
public class RequestProduct {
    
    private String mode;

    // 상품 번호 - 수정시 필수 필요
    private Long seq;

    @NotBlank
    private String gid;

    // 상품 명
    @NotBlank
    private String name;

    // 상품 요약 설명
    private String summary;

    // 상품 상세 설명 - Editor 연동 작업 예정
    private String description;

    // 소비자가 - 취소선 그려서 표기
    private int consumerPrice;

    // 판매가
    private int salePrice;
    
    // 할인 종류
    private DiscountType discountType;

    // 정가 할인 금액(1000), 할인율(10%)
    private double discount;
    
    // 최대 할인 금액
    private int maxDiscount;

    // 적립률 - 결제 금액의 할인률 적용된 상품의 판매가
    private double pointRate;

    // 최대 적립금
    private int maxPoint;
}