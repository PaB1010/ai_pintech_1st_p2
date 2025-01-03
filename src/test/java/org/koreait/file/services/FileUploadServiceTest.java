package org.koreait.file.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.file.controllers.RequestUpload;
import org.koreait.member.services.test.annotations.MockMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({"default", "test"})
@AutoConfigureMockMvc
public class FileUploadServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileUploadService service;

    @Autowired
    private ObjectMapper om;

    private MockMultipartFile[] files;

    @BeforeEach
    void init() {

        files = new MockMultipartFile[] {

            new MockMultipartFile("file", "test1.png", MediaType.IMAGE_PNG_VALUE, new byte[] {1, 2, 3}),

            new MockMultipartFile("file", "test2.png", MediaType.IMAGE_PNG_VALUE, new byte[] {1, 2, 3})
        };
    }

    @Test
    @DisplayName("모의 객체 이용 파일 업로드 기능 테스트")
    void uploadTest() {

        RequestUpload form = new RequestUpload();

        form.setGid(UUID.randomUUID().toString());

        form.setFiles(files);
        
        // 예외가 없을 경우 업로드 기능 동작
        assertDoesNotThrow(() -> service.upload(form));
    }

    @Test
    @MockMember
    @DisplayName("파일 업로드 통합 테스트")
    void uploadControllerTest() throws Exception {

        // 요청 builder
        // POST 방식이지만 요청 header 에 boundary(part 의 key 값)와 함께 multipart 로 들어감
        String body = mockMvc.perform(multipart("/api/file/upload")
                .file(files[0])
                .file(files[1])
                .param("gid", UUID.randomUUID().toString())
                        .with(csrf().asHeader()))
                // ★ print() = 요청과 응답 사이에서 어떤 정보를 가지고 요청과 응답을 하는지 자세한 정보, 디버깅 유용 ★
                .andDo(print())
                // andExpect, andExpectAll = 요청/응답 헤더, 바디등 웹기초 요소에 일치하는 데이터가 있는지 체크
                .andExpect(status().isCreated())
                // MVCResult, 주로 getRequest, get Response, getModelAndView 사용
                // 응답 객체, 응답 Body 확인용
                // getContentAsString = Body 에 있는 데이터를 문자열로 가져옴, 한글 사용시 인코딩 필요
                //                      응용해 쿠키 추가 여부도 확인 가능
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);


        /*
        // JSON 형태인 응답 Body 를 ObjectMapper 사용해 변환
        List<FileInfo> items = om.readValue(body, new TypeReference<>() {});

        boolean result1 = items.stream().anyMatch(i -> i.getFileName().equals(files[0].getOriginalFilename()));

        boolean result2 = items.stream().anyMatch(i -> i.getFileName().equals(files[1].getOriginalFilename()));

        // 둘 다 참인지 체크
        assertTrue(result1 && result2);
        */


        System.out.println(body);
    }
}