package org.koreait.global.libs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.contants.Device;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.services.CodeValueService;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Utils {

    private final HttpServletRequest request;

    // ★ 메세지 소스 주입 ★
    private final MessageSource messageSource;

    private final FileInfoService fileInfoService;

    private final CodeValueService codeValueService;

    private final MemberUtil memberUtil;

    // Browser 정보 Mobile 여부 확인
    public boolean isMobile() {

        // 요청 header -> User-Agent (Browser 정보)
        // ★ iPhone / Android 판별도 가능,
        // 어느 층의 User 가 더 많은지 판단해 App 개발에 활용 ★
        String ua = request.getHeader("User-Agent");
        
        // 해당 Pattern 이 포함되면 Mobile 판단
        String pattern = ".*(iPhone|iPod|iPad|BlackBerry|Android|Windows CE|LG|MOT|SAMSUNG|SonyEricsson).*";

        return StringUtils.hasText(ua) && ua.matches(pattern);
    }

    /**
     * mobile / front template 분리 함수
     *
     * @param path
     * @return
     */
    public String tpl(String path) {

        SiteConfig config = codeValueService.get("siteConfig", SiteConfig.class);
        
        String prefix = isMobile() ? "mobile" : "front";

        if (config != null & config.getDevice() != Device.ALL) {

            prefix = config.getDevice() == Device.MOBILE ? "mobile" : "front";
        }

        return String.format("%s/%s", prefix, path);
    }

    /**
     * 메세지 코드로 조회된 문구
     *
     * @param code
     * @return
     */
    public String getMessage(String code) {

        // 요청 header 에 있는 언어 정보(Accept-Language)로 만들어지는 Locale 객체
        Locale lo = request.getLocale();

        return messageSource.getMessage(code, null, lo);
    }

    /**
     * 메세지 코드를 배열로 받았을때
     * List 로 변환해 반환해주는 기능
     * 
     * @param codes
     * @return
     */
    public List<String> getMessages(String[] codes) {

            return Arrays.stream(codes).map(c -> {

                try {
                    return getMessage(c);

                } catch (Exception e) {
                    // ★ 예외 발생시 빈 문자열로 교체하는 방식으로 제거 ★
                    return "";
                }
                // 비어있지 않은 문자열, 즉 코드만 걸러서 가져옴
            }).filter(s -> !s.isBlank()).toList();
    }

    /**
     * REST 커맨드 객체 검증 실패시에
     * Error Code 에서 Message 추출하는 기능
     *
     * @param errors
     * @return
     */
    public Map<String, List<String>> getErrorMessages(Errors errors) {

        // 형변환해도 싱글톤 객체
        ResourceBundleMessageSource ms = (ResourceBundleMessageSource) messageSource;

        // 임시로 Message 없으면 Key 값(Message Code) 그대로 출력하는 기능을 false
        ms.setUseCodeAsDefaultMessage(false);

        try {
            // 필드별 Error Code - getFieldErrors()
            // FieldError = 커맨드 객체 검증 실패 & rejectValue(..)
            // Collectors.toMap = (Key = 필드명, Value = 메세지)
            Map<String, List<String>> messages = errors.getFieldErrors()
                    .stream()
                    .collect(Collectors.toMap(FieldError::getField, f -> getMessages(f.getCodes()), (v1, v2) -> v2));
            // v1 = 처음 값, v2 = 마지막에 들어온 값
            // -> 중복될 경우 마지막 값으로 대체되도록 처리 (put과 유사)

            // 글로벌 Error Code - getGlobalErrors()
            // GlobalError = reject(..)
            List<String> gMessages = errors.getGlobalErrors()
                    .stream()
                    // flatMap = 중첩된 stream() 펼쳐서 1차원 배열로 변환
                    .flatMap(o -> getMessages(o.getCodes()).stream())
                    .toList();

            // Global ErrorCode Field = "global" 으로 임의 고정
            if (!gMessages.isEmpty()) {

                messages.put("global", gMessages);
            }

            return messages;

        } finally {

            // 임시로 Message 없으면 Key 값(Message Code) 그대로 출력하는 기능을 false
            // 했던 것을 다시 원래대로 true 복구
            // 싱글톤이라 복구 안하면 그대로 false 로 남아서 사용하는 모든 곳에 영향
            ms.setUseCodeAsDefaultMessage(true);
        }
    }

    /**
     * IMG 출력 편의 기능
     *
     * @param seq - Server 내부 이미지
     * @param url - 외부 링크 이미지
     * @param width - 값이 있으면 썸네일
     * @param height - 값이 있으면 썸네일
     * @param mode - image : IMG 태그 출력, background : 배경 이미지 형태 출력
     * @return
     */
    public String showImage(Long seq, String url, int width, int height, String mode, String className) {

        try {
            String imageUrl = null;
            System.out.println("seq:" + seq);
            // seq가 있는 경우
            if (seq != null && seq > 0L) {

                FileInfo item = fileInfoService.get(seq);

                if (!item.isImage()) {

                    return "";
                }

                imageUrl = String.format("%s&width=%d&height=%d", item.getThumbUrl(), width, height);

            } else if (StringUtils.hasText(url)) {

                imageUrl = String.format("%s/api/file/thumb?url=%s&width=%d&height=%d", request.getContextPath(), url, width, height);

            }

            if (!StringUtils.hasText(imageUrl)) return "";

            // mode default 값 image 설정
            mode = Objects.requireNonNullElse(mode, "image");

            // className default 값 image 설정
            className = Objects.requireNonNullElse(className, "image");

            // 배경 이미지 형태
            if (mode.equals("background")) {

                return String.format("<div style='width: %dpx; height: %dpx; background:url(\"%s\") no-repeat center center; background-size:cover;' class='%s'%s></div>", width, height, imageUrl, className, seq != null && seq > 0L ? "data-seq='" + seq + "'" : "");

            } else {
                // 이미지 태그 형태
                return String.format("<img src='%s' class='%s'>", imageUrl, className);

            }
        } catch (Exception e) { }

        return "";
    }

    public String showImage(Long seq, int width, int height, String mode, String className) {
        return showImage(seq, null, width, height, mode, className);
    }

    public String showImage(Long seq, int width, int height, String className) {
        return showImage(seq, null, width, height, "image", className);
    }

    public String showBackground(Long seq, int width, int height, String mode, String className) {
        return showImage(seq, null, width, height, "background", className);
    }

    public String showImage(String url, int width, int height, String className) {
        return showImage(null, url, width, height, "image", className);
    }

    public String showImage(String url, int width, int height, String mode, String className) {
        return showImage(null, url, width, height, mode, className);
    }

    public String showBackground(String url, int width, int height, String className) {
        return showImage(null, url, width, height, "background", className);
    }

    /**
     * Message 를 Session 쪽에 저장해서 임시 팝업으로 띄움
     *
     * @param message
     */
    public void showSessionMessage(String message) {

        HttpSession session = request.getSession();

        session.setAttribute("showMessage", message);
    }

    public void removeSessionMessage() {
        HttpSession session = request.getSession();

        session.removeAttribute("showMessage");
    }

    /**
     * getParameter 편의 기능
     *
     * @param name
     * @return
     */
    public String getParam(String name) {

        return request.getParameter(name);
    }

    public String[] getParams(String name) {

        return request.getParameterValues(name);
    }

    /**
     * 줄 개행 문자(\n 혹은 \r\n)를 <br>로 변환
     *
     * @param text
     * @return
     */
    public String nl2br(String text) {

        return text == null ? "" : text.replaceAll("\\r", "")
                .replaceAll("\\n", "<br>");
    }

    /**
     * Popup 편의 기능
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public String popup(String url, int width, int height) {

        return String.format("commonLib.pup('%s', %d, %d);", url, width, height);
    }

    /**
     * 회원 / 비회원 구분 해시 (int)
     *
     * 회원 - 회원번호 / 비회원 - (IP + User-Agent)
     *
     * @return
     */
    public int getMemberHash() {

        // 회원
        if (memberUtil.isLogin()) return Objects.hash(memberUtil.getMember().getSeq());

        else { // 비회원

            String ip = request.getRemoteAddr();

            String ua = request.getHeader("User-Agent");

            return Objects.hash(ip, ua);
        }
    }

    /**
     * 전체 주소
     *
     * @param url
     * @return
     */
    public String getUrl(String url) {

        return String.format("%s://%s:%d%s%s",request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath(), url);
    }
}