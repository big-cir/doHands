package com.be.dohands.common.schedules;

import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.entity.QuestScheduleEntity;
import com.be.dohands.quest.repository.QuestScheduleRepository;
import com.be.dohands.quest.repository.UserQuestRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
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


    @Scheduled(cron = "0 0 0 * * MON")
    public void startWeeklyQuest(){
        Integer week = getThisWeek();
        List<QuestScheduleEntity> questSchedules = questScheduleRepository.findByYearAndWeek(YEAR, week);
        questSchedules.stream().map(q -> userQuestRepository.findByQuestScheduleId(q.getQuestScheduleId()))
            .flatMap(Collection::stream).forEach(userQuest -> {
                userQuest.setStatusType(StatusType.IN_PROGRESS);
                userQuestRepository.save(userQuest);
            });
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void startMonthlyQuest(){
        Integer month = getThisMonth();
        List<QuestScheduleEntity> questSchedules = questScheduleRepository.findByYearAndMonth(YEAR, month);
        questSchedules.stream().map(q -> userQuestRepository.findByQuestScheduleId(q.getQuestScheduleId()))
            .flatMap(Collection::stream).forEach(userQuest -> {
                userQuest.setStatusType(StatusType.IN_PROGRESS);
                userQuestRepository.save(userQuest);
            });
    }



    private Integer getThisWeek(){
        return LocalDate.now(ZoneId.of("Asia/Seoul")).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    private Integer getThisMonth(){
        return LocalDate.now(ZoneId.of("Asia/Seoul")).getMonthValue();
    }
}
