package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.koreait.file.controllers.RequestThumb;
import org.koreait.file.entities.FileInfo;
import org.koreait.global.configs.FileProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        Long seq = form.getSeq();
        String url = form.getUrl();

        // width & height , 50중에 더 큰 쪽으로(기본값)
        int width = Math.max(form.getWidth(), 50);
        int height = Math.max(form.getHeight(), 50);

        String thumbPath = getThumbPath(seq, url, width, height);

        File file = new File(thumbPath);

        // 이미 Thumbnail IMG 를 만든 경우
        if (file.exists()) {

            return thumbPath;
        }

        try {
            // Server 에 올라간 File
            if (seq != null && seq > 0L) {

                FileInfo item = infoService.get(seq);

                Thumbnails.of(item.getFilePath())
                        .size(width, height)
                        .toFile(file);

            } else if (StringUtils.hasText(url)) {
                // 원격 URL IMG

                String original = String.format("%s_original", thumbPath);

                byte[] bytes = restTemplate.getForObject(URI.create(url), byte[].class);

                Files.write(Paths.get(original), bytes);

                // ★ 썸네일 생성 ★
                Thumbnails.of(original)
                        .size(width, height)
                        .toFile(file);

            } else {

                thumbPath = null;
            }

        } catch (Exception e) {}

        return thumbPath;
    }

    /**
     * Thumbnail 경로
     * -> thumbs/폴더번호/seq_너비_높이.확장자
     * -> thumbs/urls/정수해시코드_너비_높이.확장자
     * 
     * seq & url 둘중 한개는 필수
     */
    public String getThumbPath(Long seq, String url, int width, int height) {

        String thumbPath = properties.getPath() + "thumbs/";
        
        // 직접 Server 에 올려서 seq 값이 있는 File
        if (seq !=null && seq > 0L) {

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
        if (!file.getParentFile().exists()) {

            // 폴더 생성
            file.getParentFile().mkdirs();
        }

        return thumbPath;
    }
}