package org.koreait.global.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 일부 Entity 의 공통 속성
 * FileInfo 에서 사용됨
 *
 */
@Data
@MappedSuperclass // ★ 상속을 통한 공통 설정 상위 클래스 ★
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseMemberEntity extends BaseEntity {

    @CreatedBy
    @Column(length=65, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(length=65, insertable = false)
    private String modifiedBy;
}