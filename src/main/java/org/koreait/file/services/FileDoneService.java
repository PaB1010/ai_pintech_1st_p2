package org.koreait.file.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.constants.FileStatus;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.repositories.FileInfoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * File 처리 완료 상태(DONE) 처리 기능
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class FileDoneService {

    private final FileInfoService infoService;
    private final FileInfoRepository repository;

    public void process(String gid, String location) {

        // 이미 Done 처리 완료된 것까지 모두 가져오도록 ALL
        List<FileInfo> items = infoService.getList(gid, location, FileStatus.ALL);

        items.forEach(item -> item.setDone(true));

        repository.saveAllAndFlush(items);
    }

    public void process(String gid) {

        process(gid, null);
    }
}
