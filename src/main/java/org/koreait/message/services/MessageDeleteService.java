package org.koreait.message.services;

import lombok.RequiredArgsConstructor;
import org.koreait.file.services.FileDeleteService;
import org.koreait.global.exceptions.UnAuthorizedException;
import org.koreait.member.libs.MemberUtil;
import org.koreait.message.entities.Message;
import org.koreait.message.repositories.MessageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Lazy
@Service
@RequiredArgsConstructor
public class MessageDeleteService {

    private final MessageInfoService infoService;

    private final MessageRepository repository;

    private final FileDeleteService fileDeleteService;

    private final MemberUtil memberUtil;

    /**
     * 쪽지 삭제 처리
     *
     * 0. 공지인 경우는 관리자인 경우만 삭제 가능
     *
     * 1. sender 쪽에서 삭제하는 경우 / mode = send
     *      deletedBySender = true
     *
     * 2. receiver 쪽에서 삭제하는 경우 / mode = receive
     *      deletedByReceiver = true
     *
     * 3. deletedBySender & deletedByReceiver 모두 true 일 경우 실제 DB 에서 삭제 (Message 쪽 삭제, File Data 함께 삭제)
     *
     * @param seq
     */
    public void process(Long seq, String mode) {

        // 발신자가 쪽지를 지우는 경우는 잘 없으므로 default 수신자 설정
        mode = StringUtils.hasText(mode) ? mode : "receive";

        // DB 삭제 여부
        boolean isProceedDelete = false;

        Message item = infoService.get(seq);

        if (item.isNotice()) {

            if (memberUtil.isAdmin()) {
                // 삭제 처리 (공지 쪽지 && 관리자일 경우)
                isProceedDelete = true;

            } else {
                // 공지이고 관리자가 아닌 경우 - 권한 없음 예외
                throw new UnAuthorizedException();
            }
        } // .endif
        
        if (mode.equals("send")) {
            // 발신자
            item.setDeletedBySender(true);
            
        } else {
            // 수신자
            item.setDeletedByReceiver(true);
        }

         if (item.isDeletedBySender() && item.isDeletedByReceiver()) {
             // 발신자 삭제 & 수신자 삭제 일경우 실제 DB 에서 삭제
             isProceedDelete = true;
         }

        // DB 삭제 진행이 필요할 경우 DB 에서 삭제 처리
         if (isProceedDelete) {

             String gid = item.getGid();

             // 1. DB 에서 삭제
             repository.delete(item);
             repository.flush();

             // 2. File 삭제
             fileDeleteService.deletes(gid);

         } else {
             // 발신자 || 수신자 한쪽만 삭제 처리한 경우 UPDATE
             repository.saveAndFlush(item);
         }
    }
}