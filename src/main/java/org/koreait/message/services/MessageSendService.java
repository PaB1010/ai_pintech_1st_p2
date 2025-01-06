package org.koreait.message.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.services.FileDoneService;
import org.koreait.member.entities.Member;
import org.koreait.member.exceptions.MemberNotFoundException;
import org.koreait.member.libs.MemberUtil;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.message.constants.MessageStatus;
import org.koreait.message.controllers.RequestMessage;
import org.koreait.message.entities.Message;
import org.koreait.message.repositories.MessageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@RequiredArgsConstructor
public class MessageSendService {

    private final MessageRepository repository;

    private final MemberUtil memberUtil;

    private final MemberRepository memberRepository;

    private final FileDoneService fileDoneService;

    public Message process(RequestMessage form) {

        String email = form.getEmail();

        // 공지가 아닐때에는 이메일로 조회, 아닐 경우 null (공지일 경우에는 이미 관리자 검증이 끝남)
        Member receiver = !form.isNotice() ? memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new) : null;

        Message message = Message.builder()
                .gid(form.getGid())
                .notice(form.isNotice())
                .subject(form.getSubject())
                .content(form.getContent())
                .sender(memberUtil.getMember())
                .receiver(receiver)
                .status(MessageStatus.UNREAD)
                .build();

        repository.saveAndFlush(message);

        // 파일 업로드 완료 처리
        fileDoneService.process(form.getGid());

        return message;
    }
}
