package org.koreait.file.services;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.entities.QFileInfo;
import org.koreait.file.exceptions.FileNotFoundException;
import org.koreait.file.repositories.FileInfoRepository;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Order.asc;

/**
 * File 조회 기능(Service)
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class FileInfoService {

    private final FileInfoRepository infoRepository;

    // application.yml_file.upload.* 접근용
    private final FileProperties properties;

    // Context path 추가용
    private final HttpServletRequest request;

    public FileInfo get(Long seq) {

        // file 단일조회 값 없을시 FileNotFond Exception
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException :: new);

        // 추가 정보 2차가공 처리
        addInfo(item);

        return item;
    }

    // 상세 조회, FileStatus.DONE 아닌 경우들 포함
    public List<FileInfo> getList(String gid, String location, FileStatus status) {
        // Filestatus Null일 경우 ALL(DONE + UNDONE) 고정
        status = Objects.requireNonNullElse(status, FileStatus.ALL);

        QFileInfo fileInfo = QFileInfo.fileInfo;

        // 추후 조건식 추가용
        BooleanBuilder andBuilder = new BooleanBuilder();

        // 필수, gid 검색
        andBuilder.and(fileInfo.gid.eq(gid));

        // 선택, location 값이 있을 경우 상세조회
        if (StringUtils.hasText(location)) {

            andBuilder.and(fileInfo.location.eq(location));
        }

        // File 작업 완료 상태 여부(DONE || UNDONE)로 조회
        if (status != FileStatus.ALL) {

            // DONE일 경우 true, UNDONE일 경우 false
            andBuilder.and(fileInfo.done.eq(status == FileStatus.DONE));
        }

        // 생성일자 오름차순으로 정렬해서 반환
        List<FileInfo> items =  (List<FileInfo>)infoRepository.findAll(andBuilder, Sort.by(asc("createdAt")));
        System.out.println(items);

        // List 추가 정보 2차 가공 처리
        items.forEach(this::addInfo);

        return items;
    }

    // 메서드 오버로드 : gid, location 으로 검색하는 경우
    // File Group 작업 완료(done = true) 된 파일 조회용 (default)
    public List<FileInfo> getList(String gid, String location) {

        return getList(gid, location, FileStatus.DONE);
    }

    // 메서드 오버로드 : gid 로만 검색하는 경우
    // File Group 작업 완료(done = true) 된 파일 조회용 (default)
    public List<FileInfo> getList(String gid) {

        return getList(gid, null);
    }

    /**
     * 추가 정보 처리 (2차 가공)
     *
     * @Transient
     * FileInfo.fileURL & FileInfo.filePath
     * 2차 가공해 완성할 목적
     *
     * File Upload 쪽에서도 사용? 외부에서도 쓸 것이라서 public
     *
     * @param item
     */
    public void addInfo(FileInfo item) {

        // filePath - Server에 올라간 실제 경로 (Download & Delete 등등 활용)
        item.setFilePath(getFilePath(item));


        // fileUrl - Browser에서 접근할 수 있는 File 주소
        item.setFileUrl(getFileUrl(item));
    }

    // 자주 사용되는 메서드라서 따로 정의
    public String getFilePath(FileInfo item) {

        Long seq = item.getSeq();

        // 확장자 없는 File 일 경우 빈 문자열로 NPE 예방 처리
        String extension = Objects.requireNonNullElse(item.getExtension(), "");

        // getFolder(seq)은 long 임에도 %s 가능
        return String.format("%s%s/%s", properties.getPath(), getFolder(seq), seq + extension);

    }

    // StackOverFlow 방지용 단일 조회 분리
    public String getFilePath(Long seq) {

        // file 단일조회 값 없을시 FileNotFond Exception
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException :: new);

        return getFilePath(item);
    }


    // 자주 사용되는 메서드라서 따로 정의
    public String getFileUrl(FileInfo item) {

        Long seq = item.getSeq();
        String extension = Objects.requireNonNullElse(item.getExtension(), "");
        return String.format("%s%s%s/%s", request.getContextPath(), properties.getUrl(), getFolder(seq), seq + extension);
    }

    // StackOverFlow 방지용 단일 조회 분리
    public String getFileUrl(Long seq) {

        // file 단일조회 값 없을시 FileNotFond Exception
        FileInfo item = infoRepository.findById(seq).orElseThrow(FileNotFoundException :: new);

        return getFileUrl(item);
    }

    // 내부 사용용
    private long getFolder(long seq) {

        return seq % 10L;
    }
}