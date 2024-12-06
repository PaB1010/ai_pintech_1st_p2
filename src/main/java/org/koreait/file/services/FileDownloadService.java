package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * File Download 기능(Service)
 *
 * File seq 로 접근해 Download
 *
 * Server가 Client에게 출력하는 흐름을
 * Body를 화면에 View 해주는 것이 아닌 File 형식으로 바꾸는
 * 응답 Header 이용
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class FileDownloadService {

    public void process(Long seq) {

        
    }
}