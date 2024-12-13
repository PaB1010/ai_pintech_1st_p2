package org.koreait.global.configs;

import lombok.RequiredArgsConstructor;
import org.koreait.member.libs.MemberUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Aware 인식
 *
 * createdBy, modifiedBy 등
 *
 * 로그인한 사용자 정보를 사용할때 무엇을 사용할지 정함
 *
 */
@Lazy
@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MemberUtil memberUtil;

    // ★ createdBy, modifiedBy 등 설정시 호출해서 유저 eamil 구함 ★
    @Override
    public Optional<String> getCurrentAuditor() {

        String email = null;
        if (memberUtil.isLogin()) {
            email = memberUtil.getMember().getEmail();
        }

        return Optional.ofNullable(email);
    }
}