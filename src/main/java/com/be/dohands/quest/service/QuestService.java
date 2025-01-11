package com.be.dohands.quest.service;

import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.dto.JobQuestDetailResponseDTO;
import com.be.dohands.quest.dto.LeaderQuestDetailResponseDTO;
import com.be.dohands.quest.dto.QuestListResponseDTO;
import com.be.dohands.quest.dto.QuestListResponseDTO.Quest;
import com.be.dohands.quest.entity.JobQuestEntity;
import com.be.dohands.quest.entity.JobQuestExpEntity;
import com.be.dohands.quest.entity.LeaderQuestEntity;
import com.be.dohands.quest.entity.LeaderQuestExpEntity;
import com.be.dohands.quest.entity.QuestScheduleEntity;
import com.be.dohands.quest.entity.UserQuestEntity;
import com.be.dohands.quest.repository.JobQuestExpRepository;
import com.be.dohands.quest.repository.JobQuestRepository;
import com.be.dohands.quest.repository.LeaderQuestExpRepository;
import com.be.dohands.quest.repository.LeaderQuestRepository;
import com.be.dohands.quest.repository.QuestScheduleRepository;
import com.be.dohands.quest.repository.UserQuestRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestService {

    private final MemberRepository memberRepository;
    private final UserQuestRepository userQuestRepository;
    private final LeaderQuestRepository leaderQuestRepository;
    private final LeaderQuestExpRepository leaderQuestExpRepository;
    private final QuestScheduleRepository questScheduleRepository;
    private final JobQuestRepository jobQuestRepository;
    private final JobQuestExpRepository jobQuestExpRepository;

    @Transactional(readOnly = true)
    public QuestListResponseDTO getQuestList(String loginId){
        Member user = memberRepository.findByLoginId(loginId).orElseThrow();
        Long userId = user.getUserId();

        List<UserQuestEntity> userQuest = userQuestRepository.findAllByUserId(userId);
        List<Quest> quests = userQuest.stream().map(e -> {

            Integer month = null;
            Integer week = null;
            String questName = null;
            Long userQuestId = userQuestRepository.findByQuestTypeAndQuestId(e.getQuestType(), e.getQuestId());

            Optional<QuestScheduleEntity> questSchedule = questScheduleRepository.findByQuestScheduleId(e.getQuestScheduleId());

            if (e.getQuestType().equals(QuestType.LEADER)){
                Optional<LeaderQuestEntity> leaderQuest = leaderQuestRepository.findByLeaderQuestId(e.getQuestId());
                if (leaderQuest.isPresent()) questName = leaderQuest.get().getQuestName();
            }
            else {
                if (questSchedule.isPresent()) {
                    if (questSchedule.get().getMonth() != null) month = questSchedule.get().getMonth();
                    else week = questSchedule.get().getWeek();
                }
            }

            return Quest.builder()
                .questId(e.getQuestId())
                .questName(questName)
                .month(month)
                .week(week)
                .questType(e.getQuestType())
                .statusType(e.getStatusType())
                .userQuestId(userQuestId)
                .build();
        }).toList();

        return QuestListResponseDTO.builder()
            .leaderQuestDetailList(quests)
            .build();
    }

    @Transactional(readOnly = true)
    public LeaderQuestDetailResponseDTO getLeaderQuestDetail(Long userQuestId){

        Optional<UserQuestEntity> userQuest = userQuestRepository.findByUserQuestId(userQuestId);

        Long questId = userQuest.get().getUserQuestId();
        Long questScheduleId = userQuest.get().getQuestScheduleId();
        Long questExpId = userQuest.get().getQuestExpId();
        StatusType status = userQuest.get().getStatusType();

        Optional<LeaderQuestEntity> leaderQuest = leaderQuestRepository.findByLeaderQuestId(questId);
        Optional<QuestScheduleEntity> questSchedule = questScheduleRepository.findByQuestScheduleId(questScheduleId);
        Optional<LeaderQuestExpEntity> leaderQuestExp = leaderQuestExpRepository.findByLeaderQuestExpId(questExpId);

        Integer exp = null;
        String notes = null;

        if (status == StatusType.DONE){
            exp = leaderQuestExp.get().getExp();
            notes = leaderQuestExp.get().getNotes();

        }
        else if (status == StatusType.FAIL){
            exp = 0;
            notes = "실패한 퀘스트입니다.";
        }

        return LeaderQuestDetailResponseDTO.builder()
            .questName(leaderQuest.get().getQuestName())
            .questType(QuestType.LEADER)
            .statusType(status)
            .month(questSchedule.get().getMonth())
            .week(questSchedule.get().getWeek())
            .maxExp(leaderQuest.get().getMaxExp())
            .medianExp(leaderQuest.get().getMedianExp())
            .maxStandard(leaderQuest.get().getMaxStandard())
            .medianStandard(leaderQuest.get().getMedianStandard())
            .exp(exp)
            .notes(notes)
            .build();
    }

    @Transactional(readOnly = true)
    public JobQuestDetailResponseDTO getJobQuestDetail(Long userQuestId){

        Optional<UserQuestEntity> userQuest = userQuestRepository.findByUserQuestId(userQuestId);

        Long questId = userQuest.get().getUserQuestId();
        Long questScheduleId = userQuest.get().getQuestScheduleId();
        Long questExpId = userQuest.get().getQuestExpId();
        StatusType status = userQuest.get().getStatusType();

        Optional<JobQuestEntity> jobQuest = jobQuestRepository.findByJobQuestId(questId);
        Optional<QuestScheduleEntity> questSchedule = questScheduleRepository.findByQuestScheduleId(questScheduleId);
        Optional<JobQuestExpEntity> jobQuestExp = jobQuestExpRepository.findByJobQuestExpId(questExpId);

        Float maxStandard = null;
        Float medianStandard = null;
        Float productivity = null;
        Integer exp = null;
        String notes = null;

        if (status == StatusType.DONE){
            maxStandard = jobQuestExp.get().getMaxStandard();
            medianStandard = jobQuestExp.get().getMedianStandard();
            productivity = jobQuestExp.get().getProductivity();
            exp = jobQuestExp.get().getExp();
            notes = jobQuestExp.get().getNotes();
        }
        else if (status == StatusType.FAIL){
            maxStandard = jobQuestExp.get().getMaxStandard();
            medianStandard = jobQuestExp.get().getMedianStandard();
            productivity = jobQuestExp.get().getProductivity();
            exp = jobQuestExp.get().getExp();
            notes = "실패한 퀘스트입니다.";
        }


        return JobQuestDetailResponseDTO.builder()
            .questName(null)
            .questType(QuestType.JOB)
            .statusType(status)
            .month(questSchedule.get().getMonth())
            .week(questSchedule.get().getWeek())
            .maxExp(jobQuest.get().getMaxExp())
            .maxStandard(maxStandard)
            .medianExp(jobQuest.get().getMedianExp())
            .medianStandard(medianStandard)
            .productivity(productivity)
            .exp(exp)
            .notes(notes)
            .build();
    }

}
