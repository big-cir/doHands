package com.be.dohands.member.service;

import com.be.dohands.member.Member;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.repository.MemberRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void saveMember(CreateMemberDto dto) {
        if (!memberRepository.existsByLoginId(dto.loginId())) {
            memberRepository.save(dto.toMember());
        } else {
            throw new RuntimeException("중복된 사용자 ID");
        }
    }

    @Transactional(readOnly = true)
    public List<Member> findMember(String name) {
        if (name != null && !name.isEmpty()) {
            Member member = memberRepository.findByName(name);
            return member != null ? List.of(member) : Collections.emptyList();
        } else {
            return memberRepository.findAll();
        }
    }

    @Transactional
    public Member modifyMember(Long userId, UpdateMemberDto dto) {
        Member member = memberRepository.findById(userId).orElseThrow();
        member.updateMember(dto.name(), dto.loginId(), dto.employeeNumber(), dto.department(),
                dto.levelId(), dto.hireDate());
        memberRepository.save(member);
        return member;
    }
}
