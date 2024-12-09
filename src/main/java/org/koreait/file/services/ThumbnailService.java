package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.controllers.RequestThumb;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Objects;

/**
 * 썸네일 생성 기능
 * 
 */
@Lazy
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(FileProperties.class)
public class ThumbnailService {

    private final FileProperties properties;
    
    private final FileInfoService infoService;
    
    // 외부 URL용
    private final RestTemplate restTemplate;

    // 썸네일 생성
    public String create(RequestThumb form) {

        return null;
    }

    /**
     * Thumbnail 경로
     * -> thumbs/폴더번호/seq_너비_높이.확장자
     * -> thumbs/urls/정수해시코드_너비_높이.확장자
     * 
     * seq & url 둘중 한개는 필수
     */
    public String getThumbPath(long seq, String url, int width, int height) {

        String thumbPath = properties.getPath() + "thumbs/";
        
        // 직접 Server에 올려서 seq 값이 있는 File
        if (seq > 0L) {

            FileInfo item = infoService.get(seq);

            thumbPath = thumbPath + String.format("%d/%d_%d_%d.%s", infoService.getFolder(seq), seq, width, height, item.getExtension());
            
        } else if (StringUtils.hasText(url)) {
            // 원격 URL IMG 인 경우
            
            // 원격 URL 에서 확장자 빼기
            String extension = url.lastIndexOf(".") == -1 ? "" : url.substring(url.lastIndexOf("."));

            // 확장자가 있는 경우
            if (StringUtils.hasText(extension)) {

                // QueryString, Hash
                extension = extension.split("[?#]]")[0];
            }

            thumbPath = thumbPath + String.format("urls/%d_%d_%d%s", Objects.hash(url), width, height, extension);
        }
        
        File file = new File(thumbPath);

        // file 의 부모쪽이 존재하지 않으면
        if (file.getParentFile().exists()) {

            // 폴더 생성
            file.getParentFile().mkdirs();
        }

        return thumbPath;
    }
}