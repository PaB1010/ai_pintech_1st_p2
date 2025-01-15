package org.koreait.file.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.*;
import org.koreait.global.exceptions.BadRequestException;
import org.koreait.global.libs.Utils;
import org.koreait.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
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

    private final FileInfoService infoService;

    private final FileDeleteService deleteService;

    private final FileDoneService doneService;

    private final ThumbnailService thumbnailService;

    private final FileImageService imageService;

    /**
     * File Upload
     *
     * @return data
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

        /**
         * 단일 File Upload
         * -> 기존 Upload File 삭제 후 새로 추가 (싱글톤)
         */
        if (form.isSingle()) {

            deleteService.deletes(form.getGid(), form.getLocation());
        }

        // 성공시 업로드한 파일 목록(List) 반환 값으로
        // ★ 요청한 쪽에서도 후속처리 필요할 경우 할 수 있게 ★
        List<FileInfo> uploadedFiles = uploadService.upload(form);

        // Upload 완료 하자마자 완료 처리 하는 경우
        if (form.isDone()) {

            doneService.process(form.getGid(), form.getLocation());
        }

        // JSONData 처리
        JSONData data = new JSONData(uploadedFiles);

        data.setStatus(HttpStatus.CREATED);

        return data;
    }

    /**
     * File Download
     *
     * @param seq
     */
    @GetMapping("/download/{seq}")
    // Body에 직접 써야하기때문에 void
    // @PathVariable("경로변수") URL 경로에서 변경 가능한 부분, Handler Adapter 가 처리
    public void download(@PathVariable("seq") Long seq) {

        downloadService.process(seq);
    }

    /**
     * File 단일 조회
     *
     * @param seq
     * @return
     */
    @GetMapping("/info/{seq}")
    public JSONData info(@PathVariable("seq") Long seq) {

        FileInfo item = infoService.get(seq);

        return new JSONData(item);
    }

    /**
     * File 목록(List) 조회
     *
     * @param gid
     * @param location
     * @return
     */
    @GetMapping({"/list/{gid}", "list/{gid}/{location}"})
    public JSONData list( // gid 는 필수, location, status 는 옵션이라 required = false
                          @PathVariable("gid") String gid,
                          @PathVariable(name = "location", required = false) String location, @RequestParam(name = "status", defaultValue = "DONE") FileStatus status) {

        List<FileInfo> items = infoService.getList(gid, location, status);

        return new JSONData(items);
    }

    /**
     * File 단일 삭제
     * @param seq
     * @return
     */
    @DeleteMapping("/delete/{seq}")
    public JSONData delete(@PathVariable("seq") Long seq) {

        FileInfo item = deleteService.delete(seq);

        return new JSONData(item);
    }

    /**
     * File 복수개 삭제
     * @param gid
     * @param location
     * @return
     */
    @DeleteMapping({"/deletes/{gid}", "/deletes/{gid}/{location}"})
    public JSONData deletes( // gid 는 필수, location 은 옵션이라 required = false
                            @PathVariable("gid") String gid,
                            @PathVariable(name = "location", required = false) String location) {

        List<FileInfo> items = deleteService.deletes(gid, location);

        return new JSONData(items);
    }

    /**
     * 썸네일 이미지
     * 커맨드 객체 정의 후
     */
    @GetMapping("/thumb")
    public void thumb(RequestThumb form, HttpServletResponse response) {

        String path = thumbnailService.create(form);

        if (!StringUtils.hasText(path)) {

            return;
        }

        File file = new File(path);

        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            /**
             * Browser IMG 출력시
             * Content Type 명시 필수적!
             *
             */
            String contentType = Files.probeContentType(file.toPath());

            response.setContentType(contentType);

            // Body 쪽에 써줌
            OutputStream out = response.getOutputStream();
            out.write(bis.readAllBytes());

        } catch (IOException e) {}
    }

    /**
     * 목록 노출 이미지 선택
     *
     * @param seq
     */
    @GetMapping("/select/{seq}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void select(@PathVariable("seq") Long seq) {

        imageService.select(seq);
    }
}