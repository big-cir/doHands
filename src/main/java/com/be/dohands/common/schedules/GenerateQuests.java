package com.be.dohands.common.schedules;

import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.entity.JobQuestEntity;
import com.be.dohands.quest.entity.LeaderQuestEntity;
import com.be.dohands.quest.entity.QuestScheduleEntity;
import com.be.dohands.quest.entity.UserQuestEntity;
import com.be.dohands.quest.repository.JobQuestRepository;
import com.be.dohands.quest.repository.LeaderQuestRepository;
import com.be.dohands.quest.repository.QuestScheduleRepository;
import com.be.dohands.quest.repository.UserQuestRepository;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class GenerateQuests {
    private final MemberRepository memberRepository;
    private final UserQuestRepository userQuestRepository;
    private final QuestScheduleRepository questScheduleRepository;
    private final JobQuestRepository jobQuestRepository;
    private final LeaderQuestRepository leaderQuestRepository;

    private final Integer YEAR = Year.now(ZoneId.of("Asia/Seoul")).getValue();

    @Scheduled(cron="0 0 0 1 1 ?")
    @Transactional
    public void generateAllQuestsForYear(){
        jobQuestRepository.findByYear(YEAR).forEach(this::generateJobQuestSchedule);
        leaderQuestRepository.findByYear(YEAR).forEach(this::generateLeaderQuestSchedule);
    }

    public void generateJobQuestSchedule(JobQuestEntity jobQuest){
        if (jobQuest.getPeriod().equals("주")) generateWeeklyJobQuests(jobQuest);
        else generateMonthlyJobQuests(jobQuest);
    }

    public void generateLeaderQuestSchedule(LeaderQuestEntity leaderQuest){
        if (leaderQuest.getPeriod().equals("주")) generateWeeklyLeaderQuests(leaderQuest);
        else generateMonthlyLeaderQuests(leaderQuest);
    }
    public void generateMonthlyJobQuests(JobQuestEntity jobQuest){
        for(int month = 1; month <= 12; month++){
            QuestScheduleEntity questSchedule = QuestScheduleEntity.builder()
                .year(YEAR)
                .month(month)
                .department(jobQuest.getDepartment())
                .questType(QuestType.JOB)
                .questId(jobQuest.getJobQuestId())
                .build();
            questScheduleRepository.save(questSchedule);
            generateUserQuests(QuestType.JOB, jobQuest.getJobQuestId(), jobQuest.getDepartment(), questSchedule, month, null);
        }
    }

    public void generateWeeklyJobQuests(JobQuestEntity jobQuest){
        for (int week = 1; week <= 52 ; week++) {
            QuestScheduleEntity questSchedule = QuestScheduleEntity.builder()
                .year(YEAR)
                .week(week)
                .department(jobQuest.getDepartment())
                .questType(QuestType.JOB)
                .questId(jobQuest.getJobQuestId())
                .build();
            questScheduleRepository.save(questSchedule);
            generateUserQuests(QuestType.JOB, jobQuest.getJobQuestId(), jobQuest.getDepartment(), questSchedule, null, week);
        }
    }

    public void generateMonthlyLeaderQuests(LeaderQuestEntity leaderQuest) {
        for(int month = 1; month <= 12; month++){
            QuestScheduleEntity questSchedule = QuestScheduleEntity.builder()
                .year(YEAR)
                .month(month)
                .department(leaderQuest.getDepartment())
                .questType(QuestType.LEADER)
                .questId(leaderQuest.getLeaderQuestId())
                .build();
            questScheduleRepository.save(questSchedule);
            generateUserQuests(QuestType.LEADER, leaderQuest.getLeaderQuestId(), leaderQuest.getDepartment(), questSchedule, month, null);
        }
    }

    public void generateWeeklyLeaderQuests(LeaderQuestEntity leaderQuest) {

        for (int week = 1; week <= 52 ; week++){
            QuestScheduleEntity questSchedule = QuestScheduleEntity.builder()
                .year(YEAR)
                .week(week)
                .department(leaderQuest.getDepartment())
                .questType(QuestType.LEADER)
                .questId(leaderQuest.getLeaderQuestId())
                .build();
            questScheduleRepository.save(questSchedule);
            generateUserQuests(QuestType.LEADER, leaderQuest.getLeaderQuestId(), leaderQuest.getDepartment(), questSchedule, null, week);
        }
    }

    public void generateUserQuests(QuestType questType, Long questId, String department, QuestScheduleEntity questSchedule){
        List<Member> departmentMembers = memberRepository.findMembersByDepartment(department);
        departmentMembers.stream().map(member -> UserQuestEntity.builder()
            .questType(questType)
            .questId(questId)
            .questExpId(null)
            .userId(member.getUserId())
            .questScheduleId(questSchedule.getQuestScheduleId())
            .statusType(StatusType.NOT_STARTED)
            .build()).forEach(userQuestRepository::save);
    }

    public void generateUserQuests(QuestType questType, Long questId, String department, QuestScheduleEntity questSchedule, Integer month, Integer week){
        List<Member> departmentMembers = memberRepository.findMembersByDepartment(department);
        departmentMembers.stream().map(member -> UserQuestEntity.builder()
            .questType(questType)
            .questId(questId)
            .questExpId(null)
            .userId(member.getUserId())
            .questScheduleId(questSchedule.getQuestScheduleId())
            .statusType(StatusType.NOT_STARTED)
            .month(month)
            .week(week)
            .build()).forEach(userQuestRepository::save);
    }

}
