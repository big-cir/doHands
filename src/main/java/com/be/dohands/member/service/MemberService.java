package com.be.dohands.member.service;

import com.be.dohands.evaluation.repository.EvaluationExpRepository;
import com.be.dohands.jobQuest.service.JobQuestService;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.MemberExpStatusDto;
import com.be.dohands.member.dto.QuestExpDto;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.leaderQuest.repository.LeaderQuestExpRepository;
import com.be.dohands.tf.repository.TfExpRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberExpRepository memberExpRepository;
    private final EvaluationExpRepository evaluationExpRepository;
    private final LeaderQuestExpRepository leaderQuestExpRepository;
    private final TfExpRepository tfExpRepository;

    private final JobQuestService jobQuestService;


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

    @Transactional(readOnly = true)
    public List<QuestExpDto> findQuestExpsById(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();

        String employeeNumber = member.getEmployeeNumber();
        List<QuestExpDto> result = findQuestExpWithoutJobQuestByEmployeeNumber(employeeNumber);

        String department = member.getDepartment();
        result.addAll(findJobQuestExpByDepartment(department));

        return result;
    }

    @Transactional(readOnly = true)
    public MemberExpStatusDto findMemberExpById(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        MemberExp memberExp = memberExpRepository.findByUserId(member.getUserId()).orElseThrow();
        return new MemberExpStatusDto(memberExp.getCurrentExp(), memberExp.getCumulativeExp());
    }

    private List<QuestExpDto> findQuestExpWithoutJobQuestByEmployeeNumber(String employeeNumber) {
        List<QuestExpDto> questExpDtos = new ArrayList<>();
        questExpDtos.addAll(
                evaluationExpRepository.findEvaluationExpsByEmployeeNumber(employeeNumber)
                        .stream()
                        .map(quest -> new QuestExpDto(quest.getGrade(), quest.getExp()))
                        .toList()
        );

        questExpDtos.addAll(
                leaderQuestExpRepository.findLeaderQuestExpsByEmployeeNumber(employeeNumber)
                        .stream()
                        .map(quest -> new QuestExpDto(quest.getQuestName(), quest.getExp()))
                        .toList()
        );

        questExpDtos.addAll(
                tfExpRepository.findTfExpsByEmployeeNumber(employeeNumber)
                        .stream()
                        .map(quest -> new QuestExpDto(quest.getProjectName(), quest.getExp()))
                        .toList()
        );

        return questExpDtos;
    }

    private List<QuestExpDto> findJobQuestExpByDepartment(String department) {
        return jobQuestService.findJobQuestExpByDepartment(department);
    }
}
