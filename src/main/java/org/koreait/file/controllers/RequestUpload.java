package org.koreait.file.controllers;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Upload 시 요청 Data 담은 커맨드 객체
 *
 */
@Data
public class RequestUpload {

    @NotBlank
    private String gid;

    private String location;

    // 단일 파일 여부
    private boolean single;

    // IMG 형식의 파일만 허용
    private boolean imageOnly;

    // Upload 하자마자 완료처리하는 경우 여부
    private boolean done;

    // 임시, 추후 set 되도록 변경
    public MultipartFile[] files;
}
