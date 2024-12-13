package org.koreait.file.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.koreait.global.entities.BaseMemberEntity;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * File 정보 -> DB 저장용
 *
 * - 작성자 정보 (BaseMemberEntity extends BaseEntity) 등
 *
 */
@Data
@Entity
// 빠른 조회 위해 index 부여
@Table(indexes = {
        @Index(name = "idx_gid", columnList = "gid, createdAt"),
        @Index(name = "idx_gid_location", columnList = "gid, location, createdAt")
})
public class FileInfo extends BaseMemberEntity implements Serializable {

    // File 등록 번호, 증감 번호
    @Id  @GeneratedValue
    private Long seq;

    // File Group Id
    @Column(length = 45, nullable = false)
    private String gid;

    // Group 내에서의 위치
    @Column(length = 45)
    private String location;

    // Upload 시 원 파일명
    @Column(length = 100, nullable = false)
    private String fileName;

    // 확장자
    @Column(length = 30)
    private String extension;

    // 파일 형식
    // EX) image/png, application/..
    @Column(length = 65)
    private String contentType;

    // 해당 Filed DB Mapping 무시
    // Entity 내부에서만 쓰는 변수, 2차 가공
    @Transient
    // Server 에서 URL 로 File 접근할 수 있는 주소 - 2차 가공
    private String fileUrl;

    // 해당 Field DB Mapping 무시
    // Entity 내부에서만 쓰는 변수, 2차 가공
    @Transient
    // Server 에 있는 File 경로 - 2차 가공
    private String filePath;
    
    // 썸네일 기본 URL
    // File 형식이 IMG 일때만 사용 예정
    @Transient
    private String thumbUrl;

    // File 과 연관된 작업이 완료되었는지 여부
    // EX) 완료되지 않아도 일단 User 에게 보여야 하므로 Server 에 선 Upload
    // 추후에 이 값이 false File 들은 Schedule 설정해 주기적으로 삭제
    // done 은 boolean 값으로 선택도가 2가지(1, 0)뿐이므로 index 부여하지 않는 것이 조회에 유리
    private boolean done;

    // 이미지 형식 여부
    // ★ Thumbnail 제작은 IMG 로만 해야해서 만든 메서드 ★
    public boolean isImage() {

        return StringUtils.hasText(contentType) && contentType.contains("image/");
    }
}