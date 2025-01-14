package com.be.dohands.quest.service;

import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.dto.JobQuestDetailResponseDTO;
import com.be.dohands.quest.dto.LeaderQuestDetailResponseDTO;
import com.be.dohands.quest.dto.QuestCountInfo;
import com.be.dohands.quest.dto.QuestListResponseDTO;
import com.be.dohands.quest.dto.QuestListResponseDTO.Quest;
import com.be.dohands.quest.dto.QuestStatisticsRequestDTO;
import com.be.dohands.quest.dto.QuestStatisticsResponseDTO;
import com.be.dohands.quest.dto.QuestStatisticsResponseDTO.QuestInfo;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
        Member user = memberRepository.findByLoginId(loginId).orElseThrow(() -> new NoSuchElementException("없는 유저입니다."));
        Long userId = user.getUserId();

        List<UserQuestEntity> userQuests = userQuestRepository.findAllByUserId(userId);
        List<Quest> quests = (userQuests.stream().map(e -> {

            Integer month = null;
            Integer week = null;
            String questName = null;
            String doneResult = null;

            QuestScheduleEntity questSchedule = questScheduleRepository.findByQuestScheduleId(e.getQuestScheduleId())
                .orElseThrow(() -> new NoSuchElementException("퀘스트 스케쥴이 없습니다."));
            UserQuestEntity userQuest = userQuestRepository.findByQuestScheduleIdAndUserId(questSchedule.getQuestScheduleId(), userId);

            if (e.getQuestType().equals(QuestType.LEADER)) {LeaderQuestEntity leaderQuest = leaderQuestRepository.findByLeaderQuestId(e.getQuestId())
                    .orElseThrow(() -> new NoSuchElementException("리더 퀘스트를 찾을 수 없습니다."));
                questName = leaderQuest.getQuestName();
                if (userQuest.getStatusType().equals(StatusType.DONE)) {
                    LeaderQuestExpEntity leaderQuestExp = leaderQuestExpRepository.findByLeaderQuestIdAndEmployeeNumber(
                        leaderQuest.getLeaderQuestId(), user.getEmployeeNumber());
                    Integer exp = leaderQuestExp.getExp();
                    if (Objects.equals(leaderQuest.getMaxExp(), exp)) doneResult = "HIGH";
                    else if (Objects.equals(leaderQuest.getMedianExp(), exp)) doneResult = "MIDDLE";
                }
            } else {
                if (questSchedule.getMonth() != null) month = questSchedule.getMonth();
                else week = questSchedule.getWeek();
                if (userQuest.getStatusType().equals(StatusType.DONE)) {
                    JobQuestEntity jobQuest = jobQuestRepository.findByJobQuestId(e.getQuestId())
                        .orElseThrow(() -> new NoSuchElementException("없는 직무 퀘스트입니다."));
                    JobQuestExpEntity jobQuestExp = jobQuestExpRepository.findByJobQuestIdAndSheetRow(jobQuest.getJobQuestId(), user.getSheetRow());
                    Integer exp = jobQuestExp.getExp();
                    if (Objects.equals(jobQuest.getMaxExp(), exp)) doneResult = "HIGH";
                    else if (Objects.equals(jobQuest.getMedianExp(), exp)) doneResult = "MIDDLE";
                    else doneResult = "LOW";
                }
            }
            return Quest.builder()
                .questId(e.getQuestId())
                .questName(questName)
                .month(month)
                .week(week)
                .questType(e.getQuestType())
                .statusType(e.getStatusType())
                .userQuestId(userQuest.getUserQuestId())
                .result(doneResult)
                .build();
        }).toList());
        return QuestListResponseDTO.builder()
            .items(quests)
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

    @Transactional(readOnly = true)
    public QuestStatisticsResponseDTO getQuestStatistics(String userId, QuestStatisticsRequestDTO request){

        List<QuestInfo> questInfoList = new ArrayList<>();

        Long uId = memberRepository.findByLoginId(userId).get().getUserId();

        List<QuestCountInfo> jobMonthQuests = userQuestRepository.totalQuestsByQuestTypeAndMonth(uId, QuestType.JOB, request.getMonth());
        getQuestInfo(questInfoList, request.getMonth(),null, QuestType.JOB, jobMonthQuests);

        List<QuestCountInfo> leaderMonthQuests = userQuestRepository.totalQuestsByQuestTypeAndMonth(uId, QuestType.LEADER, request.getMonth());
        getQuestInfo(questInfoList, request.getMonth(),null, QuestType.LEADER, leaderMonthQuests);

        List<Integer> weeks = getWeeksOfMonth(request.getYear(), request.getMonth());
        for (Integer week : weeks) {
            List<QuestCountInfo> jobWeekQuests = userQuestRepository.totalQuestsByQuestTypeAndWeek(uId, QuestType.JOB, week);
            getQuestInfo(questInfoList, null, week, QuestType.JOB, jobWeekQuests);

            List<QuestCountInfo> leaderWeekQuests = userQuestRepository.totalQuestsByQuestTypeAndWeek(uId, QuestType.LEADER, week);
            getQuestInfo(questInfoList, null, week, QuestType.LEADER, leaderWeekQuests);
        }

        return QuestStatisticsResponseDTO.builder()
            .questStatistics(questInfoList)
            .build();
    }

    private void getQuestInfo(List<QuestInfo> questInfoList, Integer month, Integer week, QuestType questType, List<QuestCountInfo> questCountInfos) {
        int done = 0;
        int total = 0;
        for (QuestCountInfo q : questCountInfos) {
            if (q.getStatus() == StatusType.DONE) done = q.getCount();
            total += q.getCount();
        }
        questInfoList.add(new QuestInfo(month, week, questType, total, done));
    }

    private static List<Integer> getWeeksOfMonth(int year, int month) {
        List<Integer> weeks = new ArrayList<>();

        LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate lastDayOfMonth = firstDayOfMonth.withDayOfMonth(firstDayOfMonth.lengthOfMonth());

        int firstWeek = firstDayOfMonth.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int lastWeek = lastDayOfMonth.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);

        if (lastDayOfMonth.get(IsoFields.WEEK_BASED_YEAR) > year){
            if (lastDayOfMonth.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)) lastDayOfMonth = lastDayOfMonth.minusDays(3);
            else if (lastDayOfMonth.getDayOfWeek().equals(DayOfWeek.TUESDAY)) lastDayOfMonth = lastDayOfMonth.minusDays(2);
            else if (lastDayOfMonth.getDayOfWeek().equals(DayOfWeek.MONDAY)) lastDayOfMonth = lastDayOfMonth.minusDays(1);
            lastWeek = lastDayOfMonth.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        }

        for (int week = firstWeek; week <= lastWeek; week++) {
            LocalDate firstDayOfWeek = LocalDate.of(year, 1, 1)
                .plusWeeks(week - 1)
                .with(DayOfWeek.MONDAY);

            LocalDate lastDayOfWeek = firstDayOfWeek.plusDays(6);

            int daysInWeek = (int) firstDayOfWeek.datesUntil(lastDayOfWeek.plusDays(1)).count();  // 1주일(7일)
            int daysInMonth = 0;

            for (LocalDate date = firstDayOfWeek; !date.isAfter(lastDayOfWeek); date = date.plusDays(1)) {
                if (date.getMonth().getValue() == month) daysInMonth++;
            }

            if (daysInMonth * 2 >= daysInWeek) weeks.add(week);

        }

        return weeks;
    }
}
