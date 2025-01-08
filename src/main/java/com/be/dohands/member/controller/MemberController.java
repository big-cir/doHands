package com.be.dohands.member.controller;

import com.be.dohands.member.Member;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.member.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/signup")
    public ResponseEntity<Void> createMember(@RequestBody CreateMemberDto createMemberDto) {
        memberService.createMember(createMemberDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<Member>> getMembers() {
        return ResponseEntity.ok(memberRepository.findAll());
    }
}
