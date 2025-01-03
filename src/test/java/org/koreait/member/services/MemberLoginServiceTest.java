package org.koreait.member.services;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.BDDMockito.*;

@SpringBootTest
@ActiveProfiles({"default", "test"})
public class MemberLoginServiceTest {

    private MemberLoginService service;

    @Mock
    private HttpServletRequest request;


    @BeforeEach
    void init() {
        //  request = mock(HttpServletRequest.class); // 모의 객체

        // 스텁 - willReturn(반환 값)
        given(request.getParameter("email")).willReturn("user01@test.org");
        given(request.getParameter("password")).willReturn("12345678");

        // 임의의 문자열 데이터
        // given(request.getParameter("email")).willReturn(anyString());

        // 정규 표현식 : 알파벳 숫자 4~20자리
        // given(request.getParameter("email")).willReturn(matches("[a-zA-Z0-9]{4,20}"));

        // 임의의 문자열 데이터
        // given(request.getParameter("password")).willReturn(matches(anyString());

        // 정규 표현식 : 8자리 이상 16자리 이하의 문자
        // given(request.getParameter("password")).willReturn(matches("[\\w{8,16}]"));

        service = new MemberLoginService(request);
    }

    @Test
    @DisplayName("행위 검증 테스트")
    void test1() {

        service.process();

        // 어떤 값이 들어오더라도 getParameter()가 1번 이상 호출됐는지 행위 검증 여부 테스트
        then(request).should(atLeast(1)).getParameter(any());
    }

    @Test
    @DisplayName("인자 캡쳐 테스트")
    void test2() {

        service.process();

        // process() 안에서 호출된 값 확인
        // 인자 캡쳐 - 지네릭 타입에 매개 변수 타입
        ArgumentCaptor<String> captor1 = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<String> captor2 = ArgumentCaptor.forClass(String.class);

        // setAttribute 에 투입된 값 확인
        then(request).should(times(2)).setAttribute(captor1.capture(), captor2.capture());

        // String key = captor1.getValue();
        List<String> key = captor1.getAllValues();

        // String value = captor2.getValue();
        List<String> value = captor2.getAllValues();

        System.out.printf("key: %s, value: %s\n", key, value);
    }
}