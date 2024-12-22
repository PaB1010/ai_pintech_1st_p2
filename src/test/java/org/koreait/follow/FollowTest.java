package org.koreait.follow;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koreait.follow.repositories.FollowRepository;
import org.koreait.follow.services.FollowService;
import org.koreait.global.paging.CommonSearch;
import org.koreait.global.paging.ListData;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
// @Transactional
// @ActiveProfiles({"default", "test"})
public class FollowTest {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FollowService followService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpSession session;

    private Member member1;
    private Member member2;
    private Member member3;

    private CommonSearch paging;

    @BeforeEach
    void init() {

//        List<Member> members = new ArrayList<>();
//        for (int i = 1; i <= 3; i++) {
//            Member member = new Member();
//            member.setEmail("user" + i + "@test.org");
//            member.setPassword("_aA123456");
//            member.setNickName("user" + i);
//            member.setName("사용자" + i);
//            member.setAddress("인천시 계양구 계양문화로" + i + "번길");
//            member.setZipCode("12345" + i);
//            member.setBirthDt(LocalDate.now().minusMonths(3));
//            member.setGender(Gender.FEMALE);
//            members.add(member);
//        }

//        memberRepository.saveAllAndFlush(members);

        member1 = memberRepository.findById(5952L).orElse(null);
        member1 = memberRepository.findById(6952L).orElse(null);
        member1 = memberRepository.findById(7952L).orElse(null);



        session.setAttribute("member", member1);

        followService.follow(member2);
        followService.follow(member3);

        paging = new CommonSearch();
    }

    /**
     *
     */
    @Test
    void test1() {

        List<Member> members = followRepository.getFollowings(member1);
        System.out.println("멤버" + members);
        assertTrue(members.stream().map(Member::getNickName).anyMatch(u -> u.equals("user2") || u.equals("user3")));
        assertEquals(members.size(), followRepository.getTotalFollowings(member1));
    }

    @Test
    void test3() {
        ListData<Member> members = followService.getFollowers(paging);

        assertEquals(0, members.getItems().size());
    }

}