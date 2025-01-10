package com.be.dohands.member.service;

import com.be.dohands.member.Member;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.MemberSlice;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.repository.MemberQueryRepository;
import com.be.dohands.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;

    @Transactional
    public void saveMember(CreateMemberDto dto) {
        if (!memberRepository.existsByLoginId(dto.loginId())) {
            memberRepository.save(dto.toMember());
        } else {
            throw new RuntimeException("중복된 사용자 ID");
        }
    }

    @Transactional(readOnly = true)
    public MemberSlice findMembers(String name, String next, int size) {
        if (next.isEmpty()) next = null;
        Long nextId = next == null ? null : Long.parseLong(next);
        if (name != null && !name.isEmpty()) {
            return memberQueryRepository.findMembersByName(name, nextId, size);
        } else {
            return memberQueryRepository.findAllMembers(nextId, size);
        }
    }

    @Transactional
    public Member modifyMember(Long userId, UpdateMemberDto dto) {
        Member member = memberRepository.findById(userId).orElseThrow();
        member.updateMember(dto.name(), dto.loginId(), dto.employeeNumber(), dto.department(),
                dto.levelId(), dto.hireDate(), dto.jobGroup());
        memberRepository.save(member);
        return member;
    }
}
