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

    // 임시, 추후 set되도록 변경
    public MultipartFile[] files;
}
