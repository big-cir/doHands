package com.be.dohands.common.schedules;

import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.entity.QuestScheduleEntity;
import com.be.dohands.quest.repository.QuestScheduleRepository;
import com.be.dohands.quest.repository.UserQuestRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.time.temporal.IsoFields;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Transactional
public class UpdateQuestStatus {
    private final UserQuestRepository userQuestRepository;
    private final QuestScheduleRepository questScheduleRepository;
    private final Integer YEAR = Year.now(ZoneId.of("Asia/Seoul")).getValue();
    private final LocalDate today = LocalDate.now();

    @Scheduled(cron = "0 0 0 * * 1")
    public void startWeeklyQuest(){
        Integer week = getThisWeek();
        List<QuestScheduleEntity> questSchedules = questScheduleRepository.findByYearAndWeek(YEAR, week);
        questSchedules.stream().map(q -> userQuestRepository.findByQuestScheduleId(q.getQuestScheduleId()))
            .flatMap(Collection::stream).forEach(userQuest -> {
                userQuest.setStatusType(StatusType.IN_PROGRESS);
                userQuestRepository.save(userQuest);
            });
    }

    @Scheduled(cron = "0 0 0 1-7 * 1")
    public void startMonthlyQuest(){
        Integer month = getThisMonth();
        List<QuestScheduleEntity> questSchedules = questScheduleRepository.findByYearAndMonth(YEAR, month);
        questSchedules.stream().map(q -> userQuestRepository.findByQuestScheduleId(q.getQuestScheduleId()))
            .flatMap(Collection::stream).forEach(userQuest -> {
                userQuest.setStatusType(StatusType.IN_PROGRESS);
                userQuestRepository.save(userQuest);
            });
    }

    // 매주 월요일 18시까지 이전 주 퀘스트의 status 가 완료로 변경되지 않은 퀘스트들은 실패로 변경
    @Scheduled(cron = "0 0 20 * * 1")
    public void updateFailedWeeklyQuests(){
        Integer week = getThisWeek()-1;
        List<QuestScheduleEntity> questSchedules = questScheduleRepository.findByYearAndWeek(YEAR, week);
        questSchedules.stream()
            .map(q -> userQuestRepository.findByQuestScheduleId(q.getQuestScheduleId()))
            .flatMap(Collection::stream).forEach(uq -> {
                if (uq.getStatusType() != StatusType.DONE)
                    uq.setStatusType(StatusType.FAIL);
                userQuestRepository.save(uq);
            });
    }

    // 매월 첫번째 평일 18시까지 이전 월 퀘스트의 status 가 완료로 변경되지 않은 퀘스트들은 실패로 변경
    @Scheduled(cron = "0 0 20 1-7 * *")
    public void updateFailedMonthlyQuests(){

        if (!isFirstWeekDay(today)) return;

        Integer month = getThisMonth()-1;
        List<QuestScheduleEntity> questSchedules = questScheduleRepository.findByYearAndMonth(YEAR, month);
        questSchedules.stream()
            .map(q -> userQuestRepository.findByQuestScheduleId(q.getQuestScheduleId()))
            .flatMap(Collection::stream).forEach(uq -> {
                if (uq.getStatusType() != StatusType.DONE)
                    uq.setStatusType(StatusType.FAIL);
                userQuestRepository.save(uq);
            });
    }

    private Integer getThisWeek(){
        return LocalDate.now(ZoneId.of("Asia/Seoul")).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    private Integer getThisMonth(){
        return LocalDate.now(ZoneId.of("Asia/Seoul")).getMonthValue();
    }

    private boolean isFirstWeekDay(LocalDate date){
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY && date.getDayOfMonth() <= 7;
    }
}
