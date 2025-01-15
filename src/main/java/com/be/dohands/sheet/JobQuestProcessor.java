package com.be.dohands.sheet;


import com.be.dohands.badge.BadgeAuto;
import com.be.dohands.common.schedules.GenerateQuests;
import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.member.service.MemberExpService;
import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.entity.JobQuestEntity;
import com.be.dohands.quest.entity.JobQuestEntity.JobQuestEntityBuilder;
import com.be.dohands.quest.entity.JobQuestExpEntity;
import com.be.dohands.quest.entity.JobQuestExpEntity.JobQuestExpEntityBuilder;
import com.be.dohands.quest.repository.JobQuestExpRepository;
import com.be.dohands.quest.repository.JobQuestRepository;
import com.be.dohands.quest.service.UserQuestService;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class JobQuestProcessor {

    private final JobQuestRepository jobQuestRepository;
    private final JobQuestExpRepository jobQuestExpRepository;
    private final MemberRepository memberRepository;
    private final GenerateQuests generateQuests;

    private final UserQuestService userQuestService;
    private final MemberExpService memberExpService;
    private final BadgeAuto badgeAuto;

    // 퀘스트 생성 시 연도 : 시트 미기재 속성이여서 시스템에서 직접 지정
    private final Integer YEAR = Year.now(ZoneId.of("Asia/Seoul")).getValue();

    /**
     * 시트 읽어와서 디비 연동하는 메서드 케이스마다 payload 내부 데이터 및 연동 엔티티 개수 등이 달라 케이스별 구현 appscript 기반 동작
     * <p>
     * TODO: 나중에 스트림으로 처리해서 알림여부 true인것만 올리는 방안으로 리펙토링
     * @return (엔티티, 알림전송여부) 리스트
     */
    public List<TransformResult<?>> readDividedSheetAndUpdateDb(Map<String, Object> payload) {
        String sheetName = (String) payload.get("sheetName");
        Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");
        List<Map<String, Object>> data1 = (List<Map<String, Object>>) dataMap.get("data1");
        List<Map<String, Object>> data2 = (List<Map<String, Object>>) dataMap.get("data2");

        log.info("Processing sheet: " + sheetName);
        List<TransformResult<?>> results = new ArrayList<>(); // 알람전송용 리스트

        if (data1 == null) {
            throw new IllegalArgumentException("잘못된 구조입니다");
        }

        //jobQuestEntity 작업 - 엔티티 하나만 생성됨
        Map<String, Object> data1Row = data1.get(0);
        int rowNumber1 = (int) data1Row.get("rowNumber");                             // rowNumber 가져오기
        List<Object> rowData1 = (List<Object>) data1Row.get("rowData");               // rowData 가져오기
        JobQuestEntity jobQuestEntity = makeJobQuestEntity(rowData1, rowNumber1);

        // jobQuestExpEntity 작업
        if (data2 != null) {
            for (Map<String, Object> row : data2) {
                int rowNumber = (int) row.get("rowNumber");
                List<Object> rowData = (List<Object>) row.get("rowData");
                TransformResult transformResult = makeJobQuestExpTransformResult(rowData,rowNumber,jobQuestEntity);
                results.add(transformResult);
            }
        }

        return results;
    }

    /**
     * JobQuestEntity 생성 메서드
     * @param : 스프레드 시트에서 받은 데이터
     * @return : JobQuestEntity
     */
    private JobQuestEntity makeJobQuestEntity(List rows, Integer sheetRow) {

        String department = rows.get(4).toString();
        String jobGroup = rows.get(5).toString();
        String period = rows.get(6).toString();
        Optional<JobQuestEntity> jobQuestEntityOptional = jobQuestRepository.findJobQuestEntityByDepartmentAndJobGroupAndPeriod(
            department, jobGroup, period);

        JobQuestEntityBuilder jobQuestEntityBuilder = JobQuestEntity.builder()
            .maxExp(TypeConversionUtil.toInteger(rows.get(0).toString()))
            .medianExp(TypeConversionUtil.toInteger(rows.get(1)))
            .department(department)
            .jobGroup(jobGroup)
            .period(period)
            .sheetRow(sheetRow)
            .year(YEAR);

        jobQuestEntityOptional.ifPresent(existMember -> jobQuestEntityBuilder.jobQuestId(existMember.getJobQuestId()));

        JobQuestEntity jobQuestEntity = jobQuestEntityBuilder.build();
        JobQuestEntity savedJobQuest = jobQuestRepository.save(jobQuestEntity);

        if (jobQuestEntityOptional.isEmpty()) {
            generateQuests.generateJobQuestSchedule(savedJobQuest);
        }

        return savedJobQuest;
    }

    /**
     * JobQuestExpEntity 생성 및 알림 전송 여부 반환 메서드
     * @param : 스프레드 시트 데이터 & 해당하는 JobQuestEntity
     * @return JobQuestExpEntity & 알림 전송 여부
     */
    public TransformResult<JobQuestExpEntity> makeJobQuestExpTransformResult(List rows, Integer sheetRow, JobQuestEntity jobQuestEntity) {

        Optional<JobQuestExpEntity> jobQuestExpEntityOptional = jobQuestExpRepository.findByJobQuestIdAndSheetRow(jobQuestEntity.getJobQuestId(), sheetRow);

        JobQuestExpEntity entity = makeJobQuestExpEntityBySheet(rows, sheetRow, jobQuestEntity,
            jobQuestExpEntityOptional);

        boolean notificationYn = isNotificationYn(jobQuestExpEntityOptional, entity.getExp());

        JobQuestExpEntity savedJobQuestExp = jobQuestExpRepository.save(entity);

        List<Member> memberList = memberRepository.findMembersByDepartmentAndJobGroup(
            jobQuestEntity.getDepartment(), jobQuestEntity.getJobGroup());

        for (Member member : memberList) {
            System.out.println(member.toString());
            userQuestService.changeStatusTypeToDoneAndInsertExpId(savedJobQuestExp.getJobQuestExpId(), QuestType.JOB,
                savedJobQuestExp.getJobQuestId(), member.getUserId(),
                savedJobQuestExp.getMonth(), savedJobQuestExp.getWeek());

            if (notificationYn) {
                memberExpService.addGivenExp(member.getUserId(), entity.getExp());
            }

            // 뱃지 조건 충족 시 획득
            badgeAuto.updateBadge(member.getUserId(), YEAR);
        }

        return TransformResult.of(savedJobQuestExp, notificationYn);
    }

    /**
     * 스프레드 시트 데이터로 JobQuestExpEntity 생성 or 수정 메서드
     * @return : JobQuestExpEntity
     */
    private JobQuestExpEntity makeJobQuestExpEntityBySheet(List rows, Integer sheetRow, JobQuestEntity jobQuestEntity, Optional<JobQuestExpEntity> jobQuestExpEntityOptional) {
        Integer givenExp = TypeConversionUtil.toInteger(rows.get(1).toString());
        JobQuestExpEntityBuilder jobQuestExpEntityBuilder = JobQuestExpEntity.builder()
            .exp(givenExp)
            .notes(rows.get(2).toString())
            .maxStandard(TypeConversionUtil.toFloat(rows.get(4).toString()))
            .medianStandard(TypeConversionUtil.toFloat(rows.get(5).toString()))
            .productivity(TypeConversionUtil.toFloat(rows.get(7).toString()))
            .month(TypeConversionUtil.toInteger(rows.get(8).toString()))
            .week(TypeConversionUtil.toInteger(rows.get(9).toString()))
            .endDate(DateUtil.toLocalDateTime(rows.get(10).toString()))
            .jobQuestId(jobQuestEntity.getJobQuestId())
            .sheetRow(sheetRow);

        jobQuestExpEntityOptional.ifPresent(existMember -> jobQuestExpEntityBuilder.jobQuestExpId(existMember.getJobQuestExpId()));

        return jobQuestExpEntityBuilder.build();
    }

    private boolean isNotificationYn(Optional<JobQuestExpEntity> jobQuestExpEntityOptional, Integer givenExp) {
        boolean notificationYn = jobQuestExpEntityOptional.isEmpty() && givenExp != null;
        if (jobQuestExpEntityOptional.isPresent() && !Objects.equals(jobQuestExpEntityOptional.get().getExp(), givenExp)) {
            notificationYn = true;
        }
        return notificationYn;
    }
}
