package com.be.dohands.sheet;


import static com.be.dohands.member.Role.ROLE_ADMIN;
import static com.be.dohands.member.Role.ROLE_USER;

import com.be.dohands.level.LevelExp;
import com.be.dohands.level.repository.LevelExpRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.Member.MemberBuilder;
import com.be.dohands.member.repository.MemberRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberProcessor extends SheetProcessor<Member>{

    private final MemberRepository memberRepository;
    private final LevelExpRepository levelExpRepository;

    @Override
    protected TransformResult<Member> transformRow(List<Object> rows, Integer sheetRow) {

        Optional<Member> memberOptional = memberRepository.findBySheetRow(sheetRow);
        String password = (rows.get(8).toString().isEmpty()) ? rows.get(7).toString() : rows.get(8).toString();
        String givenLevel = rows.get(5).toString();

        // 시트에는 직군이 없음 - 레벨에서 파싱
        String jobCategory = givenLevel.split(" ")[0];

        Long levelExpId = getLevelExpId(givenLevel);

        MemberBuilder memberBuilder = Member.builder()
            .employeeNumber(rows.get(0).toString())
            .name(rows.get(1).toString())
            .hireDate(DateUtil.toLocalDate(rows.get(2)))
            .department(rows.get(3).toString())
            .jobGroup(rows.get(4).toString())
            .levelId(levelExpId)
            .role(ROLE_USER)
            .jobCategory(jobCategory)
            .loginId(rows.get(6).toString())
            .password(password)
            .sheetRow(sheetRow);

        memberOptional.ifPresent(existMember -> {
            memberBuilder.userId(existMember.getUserId())
                    .skinId(existMember.getSkinId())
                    .characterType(existMember.getCharacterType());

            if (existMember.getRole().equals(ROLE_ADMIN)) {
                memberBuilder.role(ROLE_ADMIN);
            } else {
                memberBuilder.role(ROLE_USER);
            }
        });

        Member member = memberBuilder.build();

        Member savedMember = memberRepository.save(member);

        return TransformResult.of(savedMember, false);
    }

    private Long getLevelExpId(String givenLevel) {

        Optional<LevelExp> levelExp = levelExpRepository.findLevelExpByName(givenLevel);
        if (levelExp.isEmpty()) {
            throw new NoSuchElementException("존재하지 않는 레벨명입니다");
        }

        return levelExp.get().getLevelExpId();
    }

    @Override
    protected Member saveEntity(Member entity) {

        return memberRepository.save(entity);
    }
}
