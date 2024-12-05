package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.controllers.RequestUpload;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * File Upload 기능(Service)
 *
 * 항상 기능 구현은 CRUD - C 담당부터 작업
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileUploadService {

    private final FileProperties properties;

    private final FileInfoRepository fileInfoRepository;

    // Upload 완료된 파일의 목록(List) 반환
    public List<FileInfo> upload(RequestUpload form) {

        String gid = form.getGid();

        // gid 없을 경우 기본 값으로 "****-****-****" 형태 Random Unique ID
        gid = StringUtils.hasText(gid) ? gid : UUID.randomUUID().toString();

        String location = form.getLocation();

        MultipartFile[] files = form.getFiles();

        // File 저장할 경로
        // 현재 D:/uploads/
        String rootPath = properties.getPath();
        
        // File Upload 성공한 Files 정보
        List<FileInfo> uploadedItems = new ArrayList<>();

        for (MultipartFile file : files) {
            /* 1. File Upload 정보 - DB에 기록 S */

            // 파일명.확장자 이용해서 확장자 빼내기
            // EX) model.weights.h5면 h5
            String fileName = file.getOriginalFilename();
            // 마지막 .을 찾아서 확장자 대입
            String extension = fileName.substring(fileName.lastIndexOf("."));

            FileInfo item = new FileInfo();

            item.setGid(gid);
            item.setLocation(location);
            item.setFileName(fileName);
            item.setExtension(extension);

            // EX) image/png
            item.setContentType(file.getContentType());

            fileInfoRepository.saveAndFlush(item);

            /* 1. File Upload 정보 - DB에 기록 E */

            /* 2. File Upload 처리 S */

            long seq = item.getSeq();

            // "seq.확장자" 형태로 파일명 가공 목적
            String uploadFilename = seq + extension;

            // 균등 배분! 나눗셈 연산 이용해 10개의 Directory 분산해 File 관리
            
            long folder = seq % 10L; // 0 ~ 9, 즉 10개의 폴더

            File dir = new File(rootPath + folder);

            // Directory가 존재하지 않거나 File로만 있는 경우 새 Dir 생성
            if (!dir.exists() || !dir.isDirectory()) {

                dir.mkdirs();
            }

            // Server 에 Upload 될 파일명
            File _file = new File(dir, uploadFilename);

            try {
                // File Upload 성공
                file.transferTo(_file);

                uploadedItems.add(item);

            } catch (IOException e) {
                // File Upload 실패시, DB 저장된 Data 삭제
                fileInfoRepository.delete(item);
                fileInfoRepository.flush();;
            }
            /* 2. File Upload 처리 E */
        }

        return uploadedItems;
    }
}