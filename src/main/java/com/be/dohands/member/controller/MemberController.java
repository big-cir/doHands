package com.be.dohands.member.controller;

import com.be.dohands.common.security.CustomUserDetails;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.MemberExpStatusDto;
import com.be.dohands.member.dto.MemberInfoResponseDTO;
import com.be.dohands.member.dto.MemberResponse;
import com.be.dohands.member.dto.MemberSlice;
import com.be.dohands.member.dto.MultiCursorResult;
import com.be.dohands.member.dto.QuestExpConditionDto;
import com.be.dohands.member.dto.QuestExpDto;
import com.be.dohands.member.dto.QuestsInProgressRequestDTO;
import com.be.dohands.member.dto.QuestsInProgressResponseDTO;
import com.be.dohands.member.dto.UpdateCharacterDto;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.dto.UpdatePasswordDto;
import com.be.dohands.member.service.MemberAdminService;
import com.be.dohands.member.service.MemberService;
import com.be.dohands.quest.dto.QuestRecentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberAdminService memberAdminService;

    @PostMapping("/admin/sign-up")
    public ResponseEntity<Void> createMember(@RequestBody CreateMemberDto createMemberDto) {
        memberAdminService.saveMember(createMemberDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @GetMapping("/admin/users")
    public ResponseEntity<MemberSlice<MemberResponse>> getMembers(@RequestParam String next, @RequestParam int size,
                                                                  @RequestParam(required = false) String name) {
        return ResponseEntity.ok(memberAdminService.findMembers(name, next, size));
    }

    @PatchMapping("/admin/users/{userId}")
    public ResponseEntity<MemberResponse> updateMember(@PathVariable("userId") Long userId, @RequestBody UpdateMemberDto updateMemberDto) {
        return ResponseEntity.ok(memberAdminService.modifyMember(userId, updateMemberDto));
    }

    @GetMapping("/users/quests/experience")
    public ResponseEntity<MultiCursorResult<QuestExpDto>> getQuestsExperience(
            @RequestParam("cursor") String cursor, @RequestParam("size") int size,
            @AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        QuestExpConditionDto questExpConditionDto = new QuestExpConditionDto(cursor, size);
        return ResponseEntity.ok(memberService.findQuestExpsById(loginId, questExpConditionDto));
    }

    @GetMapping("/users/experience")
    public ResponseEntity<MemberExpStatusDto> getMemberExpStatus(@AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        return ResponseEntity.ok(memberService.findMemberExpById(loginId));
    }

    @GetMapping("/users")
    public ResponseEntity<MemberInfoResponseDTO> getMember(@AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        MemberInfoResponseDTO response = memberService.findMember(loginId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/password")
    public ResponseEntity<MemberResponse> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto, @AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        return ResponseEntity.ok(memberService.modifyPassword(updatePasswordDto, loginId));
    }

    @PatchMapping("/users/character")
    public ResponseEntity<MemberResponse> updateCharacter(@RequestBody UpdateCharacterDto updateCharacterDto, @AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        return ResponseEntity.ok(memberService.modifyCharacter(updateCharacterDto, loginId));
    }

    @GetMapping("/users/in-progress")
    public ResponseEntity<QuestsInProgressResponseDTO> getQuestsInProgress(@AuthenticationPrincipal CustomUserDetails user,
        @RequestBody QuestsInProgressRequestDTO request){
        QuestsInProgressResponseDTO response = memberService.getQuestsInProgress(user, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/recent-exp")
    public ResponseEntity<QuestRecentDto> getRecentExp(@AuthenticationPrincipal CustomUserDetails user) {
        String loginId = user.getUsername();
        return ResponseEntity.ok(memberService.findRecentQuest(loginId));
    }
}
