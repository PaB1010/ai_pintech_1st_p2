package org.koreait.member.services;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.koreait.admin.member.controllers.MemberSearch;
import org.koreait.file.entities.FileInfo;
import org.koreait.file.services.FileInfoService;
import org.koreait.global.paging.ListData;
import org.koreait.global.paging.Pagination;
import org.koreait.member.MemberInfo;
import org.koreait.member.constants.Authority;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.Member;
import org.koreait.member.entities.QMember;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.mypage.controllers.RequestProfile;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * 회원 조회 기능
 *
 * UserDetailsService & UserDetailService
 *
 * UserDetailsService
 *
 */
//@Lazy // 순환 참조 방지용
@Service
@RequiredArgsConstructor
public class MemberInfoService implements UserDetailsService {

    // 회원 조회 위해 DB
    private final MemberRepository memberRepository;

    private final FileInfoService fileInfoService;

    private final JPAQueryFactory queryFactory;

    private final HttpServletRequest request;

    private final ModelMapper modelMapper;

    // 회원 조회해서 UserDetails 구현체로 완성해 반환값 내보냄
    // 회원 정보가 필요할때마다 호출됨
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));

        List<Authorities> items = member.getAuthorities();

        if (items == null) {
            // 권한이 null 일땐 기본 권한인 USER 값
            Authorities auth = new Authorities();

            auth.setMember(member);
            auth.setAuthority(Authority.USER);

            items = List.of(auth);
        }

        // private Collection<? extends GrantedAuthority> authorities;이므로 stream 이용해 문자열로 변환
        // 무조건 문자열이어야함
        List<SimpleGrantedAuthority> authorities = items.stream().map(a -> new SimpleGrantedAuthority(a.getAuthority().name())).toList();

        // 추가 정보 처리 (2차 가공)
        addInfo(member);

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }

    public UserDetails loadUserBySeq(Long seq)

    {
        Member member = memberRepository.findById(seq).orElse(null);

        List<Authorities> items = member.getAuthorities();

        if (items == null) {
            // 권한이 null 일땐 기본 권한인 USER 값
            Authorities auth = new Authorities();

            auth.setMember(member);
            auth.setAuthority(Authority.USER);

            items = List.of(auth);
        }

        // private Collection<? extends GrantedAuthority> authorities;이므로 stream 이용해 문자열로 변환
        // 무조건 문자열이어야함
        List<SimpleGrantedAuthority> authorities = items.stream().map(a -> new SimpleGrantedAuthority(a.getAuthority().name())).toList();

        // 추가 정보 처리 (2차 가공)
        addInfo(member);

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }

    public UserDetails loadUserByNickName(String nickName)

    {
        Member member = memberRepository.findByNickName(nickName).orElse(null);

        List<Authorities> items = member.getAuthorities();

        if (items == null) {
            // 권한이 null 일땐 기본 권한인 USER 값
            Authorities auth = new Authorities();

            auth.setMember(member);
            auth.setAuthority(Authority.USER);

            items = List.of(auth);
        }

        // private Collection<? extends GrantedAuthority> authorities;이므로 stream 이용해 문자열로 변환
        // 무조건 문자열이어야함
        List<SimpleGrantedAuthority> authorities = items.stream().map(a -> new SimpleGrantedAuthority(a.getAuthority().name())).toList();

        // 추가 정보 처리 (2차 가공)
        addInfo(member);

        return MemberInfo.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .member(member)
                .authorities(authorities)
                .build();
    }

    /**
     * email 로 회원 조회
     *
     * @param email
     * @return
     */
    public Member get(String email) {
        MemberInfo memberInfo = (MemberInfo)loadUserByUsername(email);

        return memberInfo.getMember();
    }

    /**
     * 관리자용
     *
     * Email 로 조회 후 RequestProfile 로 변환해 get
     *
     * @param email
     * @return
     */
    public RequestProfile getProfile(String email) {

        Member member = get(email);

        RequestProfile profile = modelMapper.map(member, RequestProfile.class);

        List<Authority> authorities = member.getAuthorities()
                .stream()
                .map(Authorities::getAuthority).toList();

        profile.setAuthorities(authorities);

        String optionalTerms = member.getOptionalTerms();

        if (StringUtils.hasText(optionalTerms)) {

            profile.setOptionalTerms(Arrays.stream(optionalTerms.split("||")).toList());
        }

        profile.setMode("admin");

        return profile;
    }

    /**
     * 회원 목록
     *
     * @param search
     * @return
     */
    public ListData<Member> getList(MemberSearch search) {

        int page = Math.max(search.getPage(), 1);

        int limit = search.getLimit();

        limit = limit < 1 ? 20 : limit;

        // 시작 위치
        int offset = (page - 1) * limit;

        QMember member = QMember.member;

        /* 검색 처리 S */

        BooleanBuilder andBuilder = new BooleanBuilder();

        /* 키워드 검색 S */
        // 검색 옵션
        String sopt = search.getSopt();

        // 검색 키워드
        String skey = search.getSkey();

        sopt = StringUtils.hasText(sopt) ? sopt : "ALL";

        /**
         * sopt (검색 옵션)
         * ALL : 통합 검색 - 이메일 + 회원명 + 닉네임
         * NAME : 회원명 + 닉네임
         * EMAIL : 이메일
         *
         */
        if (StringUtils.hasText(skey)) {

            skey = skey.trim();

            // Predicate 구현체
            StringExpression condition;

            if (sopt.equals("EMAIL")) {

                condition = member.email;

            } else if (sopt.equals("NAME")) {

                condition = member.name.concat(member.nickName);

            } else { // 통합 검색

                condition = member.email.concat(member.name).concat(member.nickName);
            }

            andBuilder.and(condition.contains(skey));
        }

        /* 키워드 검색 E */

        // 이메일 검색
        List<String> emails = search.getEmail();

        if (emails != null && !emails.isEmpty()) {

            andBuilder.and(member.email.in(emails));
        }

        /* 권한 검색 S */
        List<Authority> authorities = search.getAuthority();

        if (authorities != null && !authorities.isEmpty()) {

            andBuilder.and(member.authorities.any().authority.in(authorities));
        }
        /* 권한 검색 E */

        /* 날짜 검색 S */

        String dateType = search.getDateType();

        // 가입일 기준
        dateType = StringUtils.hasText(dateType) ? dateType : "createdAt";

        LocalDate sDate = search.getSDate();

        LocalDate eDate = search.getEDate();

        DateTimePath<LocalDateTime> condition;

        // 탈퇴일 기준
        if (dateType.equals("deletedAt")) condition = member.deletedAt;

        else if (dateType.equals("credentialChangedAt")) condition = member.credentialChangedAt;

        else condition = member.createdAt;

        if (sDate != null) {

            andBuilder.and(condition.after(sDate.atStartOfDay()));
        }

        if (eDate != null) {

            andBuilder.and(condition.before(eDate.atTime(LocalTime.of(23, 59, 59))));
        }

        /* 날짜 검색 E */

        /* 검색 처리 E */

        // QueryDSL 활용해 Fetch Join 해 처음부터 Join 되도록 (지연 로딩 X)

        List<Member> items = queryFactory.selectFrom(member)
                .leftJoin(member.authorities)
                .fetchJoin()
                .where(andBuilder)
                .orderBy(member.createdAt.desc()) // 가입한 순서 최신순
                .offset(offset)
                .limit(limit)
                .fetch();

        // 총 회원 수 - Page 가공용
        long total = memberRepository.count(andBuilder);

        Pagination pagination = new Pagination(page, (int)total, 10, limit, request);

        return new ListData<>(items, pagination);
    }

    /**
     * 추가 정보 처리 (2차 가공)
     *
     * @param member
     */
    public void addInfo(Member member) {

        List<FileInfo> files = fileInfoService.getList(member.getEmail(), "profile");

        if (files != null && !files.isEmpty()) {

            member.setProfileImage(files.get(0));
        }
    }
}