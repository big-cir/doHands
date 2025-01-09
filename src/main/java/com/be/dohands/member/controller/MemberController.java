package com.be.dohands.member.controller;

import com.be.dohands.member.Member;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.member.service.MemberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/admin/signup")
    public ResponseEntity<Void> createMember(@RequestBody CreateMemberDto createMemberDto) {
        memberService.saveMember(createMemberDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<Member>> getMember(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(memberService.findMember(name));
    }

    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<Member> updateMember(@PathVariable("userId") Long userId, @RequestBody UpdateMemberDto updateMemberDto) {
        return ResponseEntity.ok(memberService.modifyMember(userId, updateMemberDto));
    }
}
