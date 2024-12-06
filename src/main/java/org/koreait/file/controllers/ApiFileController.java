package org.koreait.file.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.FileDownloadService;
import org.koreait.file.services.FileUploadService;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * File 도메인 RestController
 * 
 * 주로 JavaScript 에서 사용하기때문에 REST 사용
 *
 */
@Tag(name = "File API", description = "File Upload & Download & 조회 & 삭제")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class ApiFileController {

    private final Utils utils;

    private final FileUploadService uploadService;

    private final FileDownloadService downloadService;

    /**
     * File Upload
     * @return
     */
    @Operation(summary = "File Upload 처리")
    @ApiResponse(responseCode = "201", description = "File Upload 처리, 업로드 성공 시에는 업로드 완료된 File 목록 반환, 요청시 반드시 요청 헤더에 multipart/form-data 형식으로 전송")
    @Parameters({
            @Parameter(name = "gid", description = "파일 그룹 ID",required = true),
            @Parameter(name = "location", description = "파일 그룹 내에서 위치 코드"),
            @Parameter(name = "file", description = "업로드 파일, 복수개 전송 가능", required = true)
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    public JSONData upload(@RequestPart("file") MultipartFile[] files, @Valid RequestUpload form, Errors errors) {

        // gid 검증 실패시
        if (errors.hasErrors()) {

            // 올바른 gid 형식이 아니라서 BadRequest
            throw new BadRequestException(utils.getErrorMessages(errors));
        }

        form.setFiles(files);

        // 성공시 업로드한 파일 목록(List) 반환 값으로
        List<FileInfo> uploadedFiles = uploadService.upload(form);

        // JSONData 처리
        JSONData data = new JSONData(uploadedFiles);

        data.setStatus(HttpStatus.CREATED);

        return data;
    }

    /**
     * File Download
     * @param seq
     */
    @GetMapping("/download/{seq}")
    // Body에 직접 써야하기때문에 void
    // @PathVariable("경로변수") URL 경로에서 변경 가능한 부분, Handler Adapter가 처리
    public void download(@PathVariable("seq") Long seq) {

        downloadService.process(seq);
    }

    /**
     * File 단일 조회
     * @param seq
     * @return
     */
    @GetMapping("/info/{seq}")
    public JSONData info(@PathVariable("seq") Long seq) {

        return null;
    }

    /**
     * File 목록(List) 조회
     * @param gid
     * @param location
     * @return
     */
    @GetMapping({"/list/{gid}", "list/{gid}/{location}"})
    public JSONData list( // gid는 필수, location은 옵션이라 required = false
                         @PathVariable("gid") String gid,
                         @PathVariable(name = "location", required = false) String location) {

        return null;
    }

    /**
     * File 단일 삭제
     * @param seq
     * @return
     */
    @DeleteMapping("/delete/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {

        return null;
    }

    /**
     * File 복수개 삭제
     * @param gid
     * @param location
     * @return
     */
    @DeleteMapping({"/deletes/{gid}", "/deletes/{gid}/{location}"})
    public JSONData deletes( // gid는 필수, location은 옵션이라 required = false
                            @PathVariable("gid") String gid,
                            @PathVariable(name = "location", required = false) String location) {

        return null;
    }
}