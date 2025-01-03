package org.koreait.meesage.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.global.paging.ListData;
import org.koreait.member.constants.Gender;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.message.controllers.MessageSearch;
import org.koreait.message.controllers.RequestMessage;
import org.koreait.message.entities.Message;
import org.koreait.message.services.MessageInfoService;
import org.koreait.message.services.MessageSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles({"default", "test"})
public class MessageInfoServiceTest {

    @Autowired
    private MessageInfoService infoService;

    @Autowired
    private MessageSendService sendService;

    @Autowired
    private MemberUpdateService updateService;

    // 수신 이메일
    private String receiver;

    @BeforeEach
    void init() {

        for (int i = 1; i <= 2; i++) {

            RequestJoin form = new RequestJoin();
            form.setEmail("user0" + i + "@test.org");
            form.setName("이이름");
            form.setNickName("닉네임");
            form.setZipCode("0000");
            form.setAddress("주소");
            form.setAddressSub("나머지 주소");
            form.setGender(Gender.MALE);
            form.setBirthDt(LocalDate.now());
            form.setPassword("_aA123456");
            form.setConfirmPassword(form.getPassword());
            updateService.process(form);
        }
    }

    @Test
    @WithUserDetails(value="user01@test.org", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @DisplayName("쪽지 목록 조회 기능 테스트")
    void listTest() {

        createMessages();

        MessageSearch search = new MessageSearch();

        search.setMode("send");

        ListData<Message> data = infoService.getList(search);

        List<Message> items = data.getItems();

        items.forEach(System.out::println);

        assertTrue(items.size() == 10);
    }

    void createMessages() {

        for (int i = 0; i < 10; i++) {

            RequestMessage message = new RequestMessage();

            message.setEmail("user02test.org");
            message.setGid(UUID.randomUUID().toString());
            message.setSubject("제목" + i);
            message.setContent("내용" + i);

            sendService.process(message);
        }
    }
}