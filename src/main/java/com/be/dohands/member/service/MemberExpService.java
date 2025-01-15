package com.be.dohands.member.service;

import com.be.dohands.evaluation.service.EvaluationExpService;
import com.be.dohands.member.MemberExp;
import com.be.dohands.member.repository.MemberExpRepository;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.quest.dto.QuestRecentDto;
import com.be.dohands.quest.service.JobQuestService;
import com.be.dohands.quest.service.LeaderQuestExpService;
import com.be.dohands.tf.service.TfExpService;
import jakarta.transaction.Transactional;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberExpService {

    private final MemberExpRepository memberExpRepository;
    private final MemberRepository memberRepository;

    private final EvaluationExpService evaluationExpService;
    private final LeaderQuestExpService leaderQuestExpService;
    private final JobQuestService jobQuestService;
    private final TfExpService tfExpService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String[] questType = {"evaluation", "leader", "tf", "job"};

    @Transactional
    public void addGivenExp(Long userId, Integer givenExp) {
        MemberExp memberExp = memberExpRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 memberId"));

        memberExp.addToCurrentExp(givenExp);
        memberExpRepository.save(memberExp);
    }

    public void findCompleteQuestWithOutJob(String type, String employeeNumber) {
        QuestRecentDto recent = null;
        Long userId = null;
        if (type.equals(questType[0])) {
            recent = evaluationExpService.findAllMostRecent(employeeNumber);
            userId = memberRepository.findByEmployeeNumber(employeeNumber).get().getUserId();
        } else if (type.equals(questType[1])) {
            recent = leaderQuestExpService.findAllMostRecent(employeeNumber);
            userId = memberRepository.findByEmployeeNumber(employeeNumber).get().getUserId();
        } else if (type.equals(questType[2])) {
            recent = tfExpService.findAllMostRecent(employeeNumber);
            userId = memberRepository.findByEmployeeNumber(employeeNumber).get().getUserId();
        }

        String key = String.valueOf(userId);
        redisTemplate.opsForValue().set(key, getValue(recent));
    }

    public void findCompleteJobQuest(String department) {
        QuestRecentDto recent = jobQuestService.findAllMostRecent(department);
        if (recent == null) return;

        memberRepository.findMembersByDepartment(department)
                .forEach(m -> {
                    redisTemplate.opsForValue().set(String.valueOf(m.getUserId()), getValue(recent));
                });
    }

    private String getValue(QuestRecentDto recent) {
        return recent.questType() + "|" + recent.createdAt() + "|" + recent.exp();
    }

}
