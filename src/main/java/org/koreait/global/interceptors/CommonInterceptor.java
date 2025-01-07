package org.koreait.global.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.koreait.global.entities.SiteConfig;
import org.koreait.global.services.CodeValueService;
import org.koreait.member.libs.MemberUtil;
import org.koreait.message.services.MessageInfoService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * 공통 Interceptor
 *
 */
@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {

    private final CodeValueService codeValueService;

    private final MemberUtil memberUtil;

    private final MessageInfoService messageInfoService;

    /**
     * 공통 Interceptor 값 추가될 수 있으니 사이트 설정을 분리
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        setSiteConfig(modelAndView);
        setProfile(modelAndView);
    }

    /**
     * 사이트 설정
     *
     * @param mv
     */
    private void setSiteConfig(ModelAndView mv) {

        if (mv == null) return;

        SiteConfig config = Objects.requireNonNullElseGet(codeValueService.get("siteConfig", SiteConfig.class), SiteConfig::new);

        mv.addObject("siteConfig", config);
    }

    /* 회원 프로필 설정 */
    private void setProfile(ModelAndView mv) {

        if (mv == null || !memberUtil.isLogin()) return;

        // 로그인 상태일때에만 회원정보 profile 속성 업데이트
        mv.addObject("profile", memberUtil.getMember());

        // 현재 로그인한 회원의 미열람 쪽지 개수
        mv.addObject("totalUnRead", messageInfoService.totalUnRead());
    }
}