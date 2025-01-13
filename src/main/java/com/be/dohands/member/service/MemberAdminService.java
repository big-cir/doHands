package com.be.dohands.member.service;

import com.be.dohands.level.LevelExp;
import com.be.dohands.level.repository.LevelExpRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.MemberSlice;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberQueryRepository;
import com.be.dohands.member.repository.MemberRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final MemberRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;
    private final MemberExpRepository memberExpRepository;
    private final LevelExpRepository levelExpRepository;

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
    public UpdateMemberDto modifyMember(Long userId, UpdateMemberDto dto) {
        LevelExp level = findLevelExpByName(dto.level());
        Member member = memberRepository.findById(userId).orElseThrow();
        member.updateMember(
                dto.name(), dto.loginId(), dto.password(), dto.department(),
                (level != null ? level.getLevelExpId() : null), dto.jobGroup(), dto.jobCategory()
        );
        memberRepository.save(member);
        return new UpdateMemberDto(
                member.getName(), member.getLoginId(), member.getPassword(), member.getDepartment(),
                getLevelName(level), member.getJobGroup(), member.getJobCategory()
        );
    }

    private String getLevelName(LevelExp level) {
        return level != null ? level.getName() : null;
    }

    private LevelExp findLevelExpByName(String levelName) {
        return levelExpRepository.findLevelExpByName(levelName).orElse(null);
    }

    private void createMemberExp(Long userId) {
        int year = LocalDate.now().getYear();
        memberExpRepository.save(new MemberExp(year, userId));
    }
}
