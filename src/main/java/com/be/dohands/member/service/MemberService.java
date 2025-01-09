package com.be.dohands.member.service;

import com.be.dohands.evaluation.EvaluationExp;
import com.be.dohands.evaluation.repository.EvaluationExpRepository;
import com.be.dohands.evaluation.repository.EvaluationExpQueryRepository;
import com.be.dohands.jobQuest.JobQuestDetail;
import com.be.dohands.jobQuest.service.JobQuestService;
import com.be.dohands.leaderQuest.LeaderQuestExp;
import com.be.dohands.leaderQuest.repository.LeaderQuestExpQueryRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.dto.CreateMemberDto;
import com.be.dohands.member.dto.CursorResult;
import com.be.dohands.member.dto.MemberExpStatusDto;
import com.be.dohands.member.dto.MultiCursor;
import com.be.dohands.member.dto.MultiCursorResult;
import com.be.dohands.member.dto.QuestExpConditionDto;
import com.be.dohands.member.dto.QuestExpDto;
import com.be.dohands.member.dto.UpdateMemberDto;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.leaderQuest.repository.LeaderQuestExpRepository;
import com.be.dohands.tf.TfExp;
import com.be.dohands.tf.repository.TfExpQueryRepository;
import com.be.dohands.tf.repository.TfExpRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberExpRepository memberExpRepository;
    private final EvaluationExpQueryRepository evaluationExpQueryRepository;
    private final LeaderQuestExpQueryRepository leaderQuestExpQueryRepository;
    private final TfExpQueryRepository tfExpQueryRepository;

    private final JobQuestService jobQuestService;

    private static final String[] questType = {"evaluation", "leader", "tf", "job"};

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
    public MultiCursorResult<QuestExpDto> findQuestExpsById(String loginId, QuestExpConditionDto questExpConditionDto) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        MultiCursor multiCursor = MultiCursor.deserialize(questExpConditionDto.encodeCursor());

        String employeeNumber = member.getEmployeeNumber();
        String department = member.getDepartment();

        return findQuestExpWithoutJobQuestByEmployeeNumber(employeeNumber, department, multiCursor,
                questExpConditionDto.size());
    }

    @Transactional(readOnly = true)
    public MemberExpStatusDto findMemberExpById(String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        MemberExp memberExp = memberExpRepository.findByUserId(member.getUserId()).orElseThrow();
        return new MemberExpStatusDto(memberExp.getCurrentExp(), memberExp.getCumulativeExp());
    }

    private MultiCursorResult<QuestExpDto> findQuestExpWithoutJobQuestByEmployeeNumber(String employeeNumber, String department, MultiCursor multiCursor, int size) {
        List<QuestExpDto> questExpDtos = new ArrayList<>();
        Map<String, String> typeCount = new HashMap<>();

        QuestResult evaluationResult = processQuestType(
                () -> evaluationExpQueryRepository.findEvaluationExpsByEmployeeNumber(employeeNumber, multiCursor.getEvaluationExpCursor(), size),
                quest -> new QuestExpDto(quest.getEvaluationExpId(), quest.getGrade(), quest.getExp(), questType[0], quest.getCreatedAt()));
        questExpDtos.addAll(evaluationResult.items());

        QuestResult leaderQuestResult = processQuestType(
                () -> leaderQuestExpQueryRepository.findLeaderQuestExpsByEmployeeNumber(employeeNumber, multiCursor.getLeaderQuestExpCursor(), size),
                quest -> new QuestExpDto(quest.getLeaderQuestExpId(), quest.getQuestName(), quest.getExp(), questType[1], quest.getCreatedAt()));
        questExpDtos.addAll(leaderQuestResult.items());

        QuestResult tfResult = processQuestType(
                () -> tfExpQueryRepository.findTfExpsByEmployeeNumber(employeeNumber, multiCursor.getTfExpCursor(), size),
                quest -> new QuestExpDto(quest.getTfExpId(), quest.getProjectName(), quest.getExp(), questType[2], quest.getCreatedAt()));
        questExpDtos.addAll(tfResult.items());

        QuestResult jobQuestResult = processQuestType(
                () -> findJobQuestExpByDepartment(department, multiCursor.getJobQuestExpCursor(), size),
                quest -> new QuestExpDto(quest.getJobQuestDetailId(), quest.getProductivity().toString(), quest.getExp(), questType[3], quest.getCreatedAt()));
        questExpDtos.addAll(jobQuestResult.items());

        questExpDtos.sort((a, b) -> {
            int timeComparison = b.createdAt().compareTo(a.createdAt());
            return timeComparison != 0 ? timeComparison : b.itemId().compareTo(a.itemId());
        });

        List<QuestExpDto> resultItems = questExpDtos.subList(0, Math.min(questExpDtos.size(), size));

        for (QuestExpDto item : resultItems) {
            typeCount.put(item.itemType(), item.createdAt().toString());
        }

        updateCursor(questType[0], multiCursor, evaluationResult.cursorResult, typeCount);
        updateCursor(questType[1], multiCursor, leaderQuestResult.cursorResult, typeCount);
        updateCursor(questType[2], multiCursor, tfResult.cursorResult, typeCount);
        updateCursor(questType[3], multiCursor, jobQuestResult.cursorResult, typeCount);

        boolean hasNext = questExpDtos.size() > size;

        return new MultiCursorResult<>(resultItems, multiCursor.serialize(), hasNext);
    }

    private record QuestResult(List<QuestExpDto> items, CursorResult<?> cursorResult) {}

    private <T> QuestResult processQuestType(
            Supplier<CursorResult<T>> fetchData,
            Function<T, QuestExpDto> mapper
    ) {
        CursorResult<T> cursorResult = fetchData.get();
        List<QuestExpDto> items = cursorResult.getItems()
                .stream()
                .map(mapper)
                .toList();

        return new QuestResult(items, cursorResult);
    }

    private void updateCursor(
            String questType,
            MultiCursor multiCursor,
            CursorResult<?> cursorResult,
            Map<String, String> typeCount
    ) {
        String cursor = cursorResult.getCursor();
        if (cursor != null) {
            String newCursor = cursorResult.isLast() ? "-1"
                    : typeCount.getOrDefault(questType, cursor);
            multiCursor.updateCursor(questType, newCursor);
        }
    }

    private CursorResult<JobQuestDetail> findJobQuestExpByDepartment(String department, String cursor, int size) {
        return jobQuestService.findJobQuestExpByDepartment(department, cursor, size);
    }
}
