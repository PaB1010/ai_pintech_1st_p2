package org.koreait.member.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.entities.Authorities;
import org.koreait.member.entities.Member;
import org.koreait.member.entities.QAuthorities;
import org.koreait.member.repositories.AuthoritiesRepository;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.mypage.controllers.RequestProfile;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
// @ActiveProfiles({"default", "test"})
public class MemberUpdateServiceTest {

    @Autowired
    private MemberUpdateService updateService;

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ModelMapper modelMapper;

    private RequestJoin form;

    private RequestJoin _form;

    private RequestProfile profileForm;

    /*
    @BeforeEach
    void init() {

        Faker faker = new Faker(Locale.KOREA);

        form = new RequestJoin();

        // 회원 가입 데이터 생성
        form.setEmail(faker.internet().emailAddress());
        form.setPassword("_aA123456");
        form.setName(faker.name().name());
        form.setZipCode(faker.address().zipCode());
        form.setAddress(faker.address().fullAddress());
        form.setAddressSub(faker.address().buildingNumber());
        form.setNickName(faker.name().name());
        form.setGender(Gender.MALE);
        form.setRequiredTerms1(true);
        form.setRequiredTerms2(true);
        form.setRequiredTerms3(true);
        form.setOptionalTerms(List.of("advertisement"));
        // 현재 시간으로부터 20년 전
        form.setBirthDt(LocalDate.now().minusYears(20L));

        System.out.println(form);

//        RequestJoin _form = modelMapper.map(form, RequestJoin.class);
//
//        System.out.println(_form);
    }
     */

    @Test
    @DisplayName("회원 가입 기능 테스트")
    void joinTest() {

        updateService.process(form);

        Member member = memberRepository.findByEmail(form.getEmail()).orElse(null);

        System.out.println(member);
        System.out.println(member.getAuthorities());
        System.out.println(form);
    }

    @Test
    void authoritiesTest() {

        updateService.process(form);

        Member member = memberRepository.findByEmail(form.getEmail()).orElse(null);

        System.out.println(member);

        QAuthorities qAuthorities = QAuthorities.authorities;

        List<Authorities> items = (List<Authorities>) authoritiesRepository.findAll(qAuthorities.member.eq(member));
    }

    @Test
    void profileTest() {

        profileForm = new RequestProfile();

        profileForm.setName("변경" + form.getName());
        profileForm.setNickName("변경" + form.getNickName());
        profileForm.setPassword("AA" + form.getPassword());
        profileForm.setConfirmPassword(profileForm.getPassword());

        System.out.println(profileForm);

//        _form.setName(profileForm.getName());
//        _form.setNickName(profileForm.getNickName());
//        _form.setPassword(profileForm.getPassword());
//        _form.setConfirmPassword(profileForm.getConfirmPassword());
//
//        updateService.process(_form);

        updateService.process(profileForm);

        // Member member = memberRepository.findByEmail(form.getEmail()).orElse(null);
    }

    @Test
    void profileTest2() {

        profileForm = new RequestProfile();

        Member member = memberRepository.findByEmail("user01@test.org").orElse(null);

        // System.out.println(member);

        profileForm.setName("변경된 " + Objects.requireNonNull(member).getName());
        profileForm.setNickName("변경된 " + member.getNickName());
        profileForm.setPassword("AA" + member.getPassword());
        profileForm.setConfirmPassword(profileForm.getPassword());

        modelMapper.map(profileForm, Member.class);

        // System.out.println(profileForm);

        updateService.process(profileForm);
    }
}