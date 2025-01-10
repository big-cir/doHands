package com.be.dohands.member.service;

import static com.be.dohands.member.dto.QuestResult.processQuestType;

import com.be.dohands.evaluation.repository.EvaluationExpQueryRepository;
import com.be.dohands.quest.entity.JobQuestExpEntity;
import com.be.dohands.quest.service.JobQuestService;
import com.be.dohands.quest.repository.LeaderQuestExpQueryRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.dto.UpdateProfileDto;
import com.be.dohands.member.dto.CursorResult;
import com.be.dohands.member.dto.MemberExpStatusDto;
import com.be.dohands.member.dto.MultiCursor;
import com.be.dohands.member.dto.MultiCursorResult;
import com.be.dohands.member.dto.QuestExpConditionDto;
import com.be.dohands.member.dto.QuestExpDto;
import com.be.dohands.member.dto.QuestResult;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.tf.repository.TfExpQueryRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Transactional
    public Member modifyProfile(UpdateProfileDto updateProfileDto, String loginId) {
        Member member = memberRepository.findByLoginId(loginId).orElseThrow();
        member.updateProfile(updateProfileDto.changePassword(), updateProfileDto.characterType());
        return memberRepository.save(member);
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
                quest -> new QuestExpDto(quest.getJobQuestExpId(), quest.getProductivity().toString(), quest.getExp(), questType[3], quest.getCreatedAt()));
        questExpDtos.addAll(jobQuestResult.items());

        questExpDtos.sort((a, b) -> {
            int timeComparison = b.createdAt().compareTo(a.createdAt());
            return timeComparison != 0 ? timeComparison : b.itemId().compareTo(a.itemId());
        });

        List<QuestExpDto> resultItems = questExpDtos.subList(0, Math.min(questExpDtos.size(), size));

        for (QuestExpDto item : resultItems) {
            typeCount.put(item.itemType(), item.createdAt().toString());
        }

        updateCursor(questType[0], multiCursor, evaluationResult.cursorResult(), typeCount);
        updateCursor(questType[1], multiCursor, leaderQuestResult.cursorResult(), typeCount);
        updateCursor(questType[2], multiCursor, tfResult.cursorResult(), typeCount);
        updateCursor(questType[3], multiCursor, jobQuestResult.cursorResult(), typeCount);

        boolean hasNext = questExpDtos.size() > size;

        return new MultiCursorResult<>(resultItems, multiCursor.serialize(), hasNext);
    }

    private void updateCursor(String questType, MultiCursor multiCursor, CursorResult<?> cursorResult, Map<String, String> typeCount) {
        String cursor = cursorResult.getCursor();
        if (cursor != null) {
            String newCursor = cursorResult.isLast() ? "-1"
                    : typeCount.getOrDefault(questType, cursor);
            multiCursor.updateCursor(questType, newCursor);
        }
    }

    private CursorResult<JobQuestExpEntity> findJobQuestExpByDepartment(String department, String cursor, int size) {
        return jobQuestService.findJobQuestExpByDepartment(department, cursor, size);
    }
}
