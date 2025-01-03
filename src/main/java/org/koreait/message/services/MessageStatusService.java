package org.koreait.message.services;

import lombok.RequiredArgsConstructor;
import org.koreait.message.constants.MessageStatus;
import org.koreait.message.entities.Message;
import org.koreait.message.repositories.MessageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 쪽지 조회 여부 전환 기능
 *
 */
@Lazy
@Service
@RequiredArgsConstructor
public class MessageStatusService {

    private final MessageInfoService infoService;

    private final MessageRepository repository;

    /**
     * 미열람 -> 열람 형태 변경
     *
     * @param seq
     */
    public void change(Long seq) {

        Message item = infoService.get(seq);

        // 수신한 쪽지만 열람 상태로 변경
        if (item.isReceived()) item.setStatus(MessageStatus.READ);

        repository.saveAndFlush(item);
    }
}