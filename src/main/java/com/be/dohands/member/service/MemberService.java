package com.be.dohands.member.service;

import com.be.dohands.member.Member;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public void createMember(CreateMemberDto dto) {
        if (!memberRepository.existsById(dto.id())) {
            Member member = new Member(dto.id(), dto.employeeNumber(), dto.department(), dto.name());
            memberRepository.save(member);
        } else {
            throw new RuntimeException("중복된 사용자 ID");
        }
    }
}
