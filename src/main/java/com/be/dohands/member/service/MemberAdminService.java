package com.be.dohands.member.service;

import com.be.dohands.level.LevelExp;
import com.be.dohands.level.service.LevelExpService;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.MemberResponse;
import com.be.dohands.member.dto.MemberSlice;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberQueryRepository;
import com.be.dohands.member.repository.MemberRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final MemberExpRepository memberExpRepository;
    private final LevelExpService levelExpService;

    @Transactional
    public void saveMember(CreateMemberDto dto) {
        if (!memberRepository.existsByLoginId(dto.loginId())) {
            LevelExp level = findLevelExpByName(dto.level());
            Member member = memberRepository.save(dto.toMember(level != null ? level.getLevelExpId() : null));
            createMemberExp(member.getUserId());
        } else {
            throw new RuntimeException("중복된 사용자 ID");
        }
    }

    @Transactional(readOnly = true)
    public MemberSlice<MemberResponse> findMembers(String name, String next, int size) {
        if (next.isEmpty()) next = null;
        Long nextId = next == null ? null : Long.parseLong(next);
        if (name != null && !name.isEmpty()) {
            MemberSlice<Member> slice = memberQueryRepository.findMembersByName(name, nextId, size);
            return new MemberSlice<>(
                    slice.getMembers()
                            .stream()
                            .map(m -> {
                                LevelExp level = levelExpService.findLevelExp(m.getLevelId());
                                return new MemberResponse(m, getLevelName(level));
                            })
                            .toList(),
                    slice.getNext(),
                    slice.isHasNext());
        } else {
            MemberSlice<Member> slice = memberQueryRepository.findAllMembers(nextId, size);
            return new MemberSlice<>(
                    slice.getMembers()
                            .stream()
                            .map(m -> {
                                Long levelId = m.getLevelId();
                                LevelExp level = (levelId != null) ? levelExpService.findLevelExp(levelId) : null;
                                return new MemberResponse(m, getLevelName(level));
                            })
                            .toList(),
                    slice.getNext(),
                    slice.isHasNext());
        }
    }

    @Transactional
    public MemberResponse modifyMember(Long userId, UpdateMemberDto dto) {
        LevelExp level = findLevelExpByName(dto.level());
        Member member = memberRepository.findById(userId).orElseThrow();
        member.updateMember(
                dto.name(), dto.loginId(), dto.password(), dto.department(),
                (level != null ? level.getLevelExpId() : null), dto.jobGroup(), dto.jobCategory()
        );
        memberRepository.save(member);
        return new MemberResponse(member, getLevelName(level));
    }

    private String getLevelName(LevelExp level) {
        return level != null ? level.getName() : null;
    }

    private LevelExp findLevelExpByName(String levelName) {
        return levelExpService.findByName(levelName);
    }

    private void createMemberExp(Long userId) {
        int year = LocalDate.now().getYear();
        memberExpRepository.save(new MemberExp(year, userId));
    }
}
