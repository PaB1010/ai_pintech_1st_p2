package org.koreait.member.controllers;

import lombok.RequiredArgsConstructor;
import org.koreait.global.rests.JSONData;
import org.koreait.mypage.services.FollowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class ApiMemberController {

    private final FollowService service;

    @GetMapping("/follow/{seq}")
    public JSONData follow(@PathVariable("seq") Long seq) {

        service.follow(seq);

        return new JSONData();
    }

    @GetMapping("/unfollow/{seq}")
    public JSONData unfollow(@PathVariable("seq") Long seq) {

        service.unfollow(seq);

        return new JSONData();
    }
}
