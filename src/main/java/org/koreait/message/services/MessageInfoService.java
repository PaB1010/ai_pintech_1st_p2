package org.koreait.message.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.libs.Utils;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.member.entities.Member;
import org.koreait.member.libs.MemberUtil;
import org.koreait.message.controllers.MessageSearch;
import org.koreait.message.entities.Message;
import org.koreait.message.entities.QMessage;
import org.koreait.message.exceptions.MessageNotFoundException;
import org.koreait.message.repositories.MessageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Lazy
@Service
@RequiredArgsConstructor
public class MessageInfoService {
    
    private final MessageRepository messageRepository;

    // 수신쪽 fetchJoin 용
    private final JPAQueryFactory queryFactory;

    private final HttpServletRequest request;

    private final Utils utils;

    private final MemberUtil memberUtil;

    private final FileInfoService fileInfoService;

    /**
     * 쪽지 개별 조회
     * 
     * @param seq
     * @return
     */
    public Message get(Long seq) {

        BooleanBuilder builder = new BooleanBuilder();

        BooleanBuilder orBuilder = new BooleanBuilder();

        QMessage message = QMessage.message;

        builder.and(message.seq.eq(seq));

        if (!memberUtil.isAdmin()) {

            Member member = memberUtil.getMember();

            BooleanBuilder orBuilder2 = new BooleanBuilder();

            BooleanBuilder andBuilder = new BooleanBuilder();

            // (공지 쪽지 & 수신자 null) || 현재 로그인중인 멤버가 받은 쪽지
            orBuilder2.or(andBuilder.and(message.notice.eq(true)).and(message.receiver.isNull()))
                            .or(message.receiver.eq(member));

            orBuilder.or(message.sender.eq(member))
                            .or(orBuilder2);



            builder.and(orBuilder);
        }


        Message item = messageRepository.findOne(builder).orElseThrow(MessageNotFoundException::new);

        // 추가 정보 2차 가공 처리
        addInfo(item);
        
        return item;
    }

    /**
     * 쪽지 목록 조회
     * 
     * @param search
     * @return
     */
    public ListData<Message> getList(MessageSearch search) {

        int page = Math.max(search.getPage(), 1);
        int limit = search.getLimit();

        limit = limit < 1 ? 20 : limit;

        int offset = limit * (page - 1);

        /* 검색 조건 처리 S */
        BooleanBuilder andBuilder = new BooleanBuilder();

        QMessage message = QMessage.message;

        String mode = search.getMode();

        Member member = memberUtil.getMember();

        mode = StringUtils.hasText(mode) ? mode : "receive";

        // send = 발신 쪽지 목록
        // receive = 수신 쪽지 목록 (공지일 경우 없을 수도 있음)
        if (mode.equals("send")) {

            andBuilder.and(message.sender.eq(member));

        } else {
            // 공지가 아닐 경우에만 receive 추가
            BooleanBuilder orBuilder = new BooleanBuilder();

            BooleanBuilder andBuilder1 = new BooleanBuilder();

            // (공지 쪽지 & 수신자 null) || 현재 로그인중인 멤버가 받은 쪽지
            orBuilder.or(andBuilder1.and(message.notice.eq(true)).and(message.receiver.isNull()))
                    .or(message.receiver.eq(member));

            andBuilder.and(orBuilder);
        }

        andBuilder.and(mode.equals("send") ? message.deletedBySender.eq(false) : message.deletedByReceiver.eq(false));

        /* 발신인 조건 검색 */
        List<String> sender = search.getSender();

        // 수신 쪽지함 모드 일 경우에만
        if (mode.equals("receive") && sender != null && !sender.isEmpty()) {

            // 발신인 이메일로 조회하는 기능
            andBuilder.and(message.sender.email.in(sender));
        }
        /* 검색 조건 처리 E */

        /* 키워드 검색 처리 S */
        String sopt = search.getSopt();
        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";

        if (StringUtils.hasText(skey)) {

            // 제목 검색 : 제목+내용 검색
            StringExpression condition =  sopt.equals("SUBJECT") ? message.subject : message.subject.concat(message.content);

            andBuilder.and(condition.contains(skey.trim()));
        }
        /* 키워드 검색 처리 E */

        // 수신인 즉시 조인(fetch Join) 후 가져오기 위해 사용
        List<Message> items = queryFactory.selectFrom(message)
                .leftJoin(message.receiver)
                .fetchJoin()
                .where(andBuilder)
                .limit(limit)
                .offset(offset)
                // 1차 정렬 : 공지 최신순 & 2차 정렬 : 쪽지 생성일 최신순
                // index 넣어서 빠른 조회
                .orderBy(message.notice.desc(), message.createdAt.desc())
                .fetch();

        items.forEach(this::addInfo);

        long total = messageRepository.count(andBuilder);

        // int ranges = utils.isMobile() ? 5 : 10;

        // 모바일일 경우 5페이지씩, 아닐 경우 10페이지씩
        Pagination pagination = new Pagination(page, (int)total, utils.isMobile() ? 5 : 10, limit, request);
        
        return new ListData<>(items, pagination);
    }

    /**
     * 추가 정보 2차 가공 처리
     *
     * 첨부 파일 관련
     *
     * @param item
     */
    private void addInfo(Message item) {

        String gid = item.getGid();

        Member member = memberUtil.getMember();

        item.setEditorImages(fileInfoService.getList(gid, "editor"));
        item.setAttachFiles(fileInfoService.getList(gid, "attach"));

        // 로그인한 본인이 받은 쪽지인지 여부
        item.setReceived(
                // 공지 쪽지 && 수신자 Null || 로그인한 회원 == 수신자 회원
                (item.isNotice() && item.getReceiver() == null)
                        || item.getReceiver().getSeq().equals(member.getSeq())
        );
        // item.setReceived(Objects.equals(item.getReceiver().getSeq(), memberUtil.getMember().getSeq()));

        // 삭제 가능 여부
        // (공지 && 관리자) || (공지 아니고 && 현재 로그인한 회원이 받은 쪽지) || 현재 로그인한 회원이 보낸 쪽지
        boolean deletable = (item.isNotice() && memberUtil.isAdmin())
                || (!item.isNotice() && (item.getSender().getSeq().equals(member.getSeq())
                || item.getReceiver().getSeq().equals(member.getSeq())));

        item.setDeletable(deletable);
    }
}