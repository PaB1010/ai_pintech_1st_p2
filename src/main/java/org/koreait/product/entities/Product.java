package org.koreait.product.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.entities.BaseMemberEntity;
import org.koreait.product.constants.DiscountType;

import java.util.List;

@Data
@Entity
public class Product extends BaseMemberEntity { // 주로 관리자가 이용할 것이라 BaseMemberEntity 상속

    // 상용 솔루션의 경우 seq 가 아닌 상품 별도 관리 코드가 있음

    @Id
    @GeneratedValue
    private Long seq;

    // true : 소비자 페이지에도 상품 노출
    // false : 관리자 페이지에만 상품 노출
    // @Column(nullable = false)
    private boolean open;

    @Column(length = 45, nullable = false)
    private String gid;

    // 상품명
    // byte 150, 한글은 3byte 이므로 한글 50자
    @Column(length = 150, nullable = false)
    private String name;

    // 상품 요약 설명
    private String summary;

    // 상품 상세 설명
    @Lob
    private String description;

    // 할인 종류
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DiscountType discountType;

    // 정가 할인 금액(1000) || 할인율(10.5%)
    private double discount;

    // 최대 할인 금액
    private int maxDiscount;

    // 적립률 - 결제 금액에서 할인률 적용된 상품의 판매가
    private double pointRate;

    // 최대 적립금
    private int maxPoint;

    // 상품 상세 메인 이미지
    // 2차 가공용, gid = main
    @Transient
    private List<FileInfo> mainImages;

    // 상품 목록 이미지
    // 2차 가공용, gid = list
    @Transient
    private List<FileInfo> listImages;

    // 상세 설명 이미지
    // 2차 가공용, gid = editor
    @Transient
    private List<FileInfo> editorImages;
}