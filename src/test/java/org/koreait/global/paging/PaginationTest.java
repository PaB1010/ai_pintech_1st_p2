package org.koreait.global.paging;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@ActiveProfiles({"default", "test"})
public class PaginationTest {

    @Mock // 가짜 객체 생성 (임시 interface 구현체)
    private HttpServletRequest request;

    // 현재 가짜 객체인 request 는 null 상태라서 getQueryString 불가하므로
    // Test 위해 사전 작업 필요
    @BeforeEach
    void init() {

        // import static org.mockito.BDDMockito.*;
        // Stub - 가짜 Data
        // given(request.getQueryString()).willReturn("query=블로그&test1=1&test2=2&page=3");
        given(request.getQueryString()).willReturn(null);

    }

    @Test
    void test1() {

        // public Pagination(int page, int total, int ranges, int limit)
        Pagination pagination = new Pagination(23, 9999, 10, 20, request);

        System.out.println(pagination);

        pagination.getPages().forEach(s -> System.out.println(Arrays.toString(s)));
    }
}