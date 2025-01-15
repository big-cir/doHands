package com.be.dohands.sheet;


import com.be.dohands.badge.BadgeAuto;
import com.be.dohands.common.schedules.GenerateQuests;
import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.member.service.MemberExpService;
import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.entity.LeaderQuestEntity;
import com.be.dohands.quest.entity.LeaderQuestEntity.LeaderQuestEntityBuilder;
import com.be.dohands.quest.entity.LeaderQuestExpEntity;
import com.be.dohands.quest.entity.LeaderQuestExpEntity.LeaderQuestExpEntityBuilder;
import com.be.dohands.quest.repository.LeaderQuestExpRepository;
import com.be.dohands.quest.repository.LeaderQuestRepository;
import com.be.dohands.quest.service.UserQuestService;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class LeaderQuestProcessor {

    private final MemberRepository memberRepository;
    private final LeaderQuestRepository leaderQuestRepository;
    private final LeaderQuestExpRepository leaderQuestExpRepository;
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
    /**
     * TODO :
     * 1. 부여 경험치 추가 시, statusType -> done (userQuest) & expId 추가
     * 2. 추가 시, cuurentExp ++ (userExp) - 인사평가 & 전사 프로젝트도 해줘야함
     */
    public List<TransformResult<?>> readDividedSheetAndUpdateDb(Map<String, Object> payload) {
        String sheetName = (String) payload.get("sheetName");
        Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");
        List<Map<String, Object>> data1 = (List<Map<String, Object>>) dataMap.get("data1");
        List<Map<String, Object>> data2 = (List<Map<String, Object>>) dataMap.get("data2");
        List<Map<String, Object>> data3 = (List<Map<String, Object>>) dataMap.get("data3");

        log.info("Processing sheet: " + sheetName);
        List<TransformResult<?>> results = new ArrayList<>(); // 알람전송용 리스트

        if (data1 == null) {
            throw new IllegalArgumentException("잘못된 구조입니다");
        }

        Map<String, Object> data1Row = data1.get(0);
        List<Object> rowData1 = (List<Object>) data1Row.get("rowData");
        String department = rowData1.get(0).toString();

        //leaderQuestEntity 작업
        if (data3 != null) {
            for (Map<String, Object> row : data3) {
                int rowNumber = (int) row.get("rowNumber");
                List<Object> rowData = (List<Object>) row.get("rowData");
                System.out.println(rowData.toString());
                if (rowData.get(0).toString().isEmpty() || rowData.get(0).toString().equals("합산")) {
                    continue;
                }
                Optional<LeaderQuestEntity> leaderQuestEntityOptional = leaderQuestRepository.findBySheetRow(rowNumber);

                //if, 새로 생성된 거면 -> leaderQuestExp 생성
                // userQuest 생성
                // QS 생성
                if (leaderQuestEntityOptional.isEmpty()) {
                    LeaderQuestEntity leaderQuestEntity = makeLeaderQuestEntity(rowData, rowNumber, department,
                        leaderQuestEntityOptional);
                    generateQuests.generateLeaderQuestSchedule(leaderQuestEntity);
                } else {
                    makeLeaderQuestEntity(rowData, rowNumber,department, leaderQuestEntityOptional);
                }
            }
        }

//         leaderQuestExpEntity - 시트 데이터 있는 경우에만 실시
        if (data2 != null) {
            for (Map<String, Object> row : data2) {
                int rowNumber = (int) row.get("rowNumber");
                List<Object> rowData = (List<Object>) row.get("rowData");

                if (rowData.get(4).toString().isEmpty()) {
                    continue;
                }

                TransformResult transformResult = createLeaderQuestExpTransformResult(rowData,rowNumber);
                results.add(transformResult);
            }
        }

        return results;
    }

    /**
     * LeaderQuestEntity 생성 메서드
     * @param : 스프레드 시트에서 받은 데이터
     * @return : JobQuestEntity
     */
    private LeaderQuestEntity makeLeaderQuestEntity(List rows, Integer sheetRow, String department, Optional<LeaderQuestEntity> leaderQuestEntityOptional) {


        LeaderQuestEntityBuilder entityBuilder = LeaderQuestEntity.builder()
            .department(department)
            .questName(rows.get(0).toString())
            .period(rows.get(1).toString())
            .proportion((int) (TypeConversionUtil.toFloat(rows.get(2).toString())*100))
            .exp(TypeConversionUtil.toInteger(rows.get(3).toString()))
            .maxExp(TypeConversionUtil.toFlatToInteger(rows.get(4).toString()))     // 시트에서 값 반올림 자동으로 한 건 소수점도 같이 주네...
            .medianExp(TypeConversionUtil.toFlatToInteger(rows.get(5).toString()))
            .maxStandard(rows.get(6).toString())
            .medianStandard(rows.get(7).toString())
            .notes(rows.get(8).toString())
            .sheetRow(sheetRow)
            .year(YEAR);

        leaderQuestEntityOptional.ifPresent(existMember -> entityBuilder.leaderQuestId(existMember.getLeaderQuestId()));

        LeaderQuestEntity leaderQuestEntity = entityBuilder.build();


        return leaderQuestRepository.save(leaderQuestEntity);
    }

    /**
     * LeaderQuestExpEntity 생성 및 알림 전송 여부 반환 메서드
     *
     * userQuest StatusType -> Done
     * userExp currentExp ++
     * @param : 스프레드 시트 데이터 & 해당하는 LeaderQuestExpEntity
     * @return LeaderQuestExpEntity & 알림 전송 여부
     */
    public TransformResult<LeaderQuestExpEntity> createLeaderQuestExpTransformResult(List rows, Integer sheetRow) {

        Optional<LeaderQuestExpEntity> entityOptional = leaderQuestExpRepository.findBySheetRow(sheetRow);

        // 경험치 변경 여부로 알림 전송 여부 판단
        LeaderQuestExpEntity entity = makeLeaderQuestExpEntityBySheet(rows, sheetRow, entityOptional);

        boolean notificationYn = isNotificationYn(entityOptional, entity.getExp());
        LeaderQuestExpEntity savedLeaderQuestExp = leaderQuestExpRepository.save(entity);

        Member member = memberRepository.findByEmployeeNumber(savedLeaderQuestExp.getEmployeeNumber())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원번호"));

        userQuestService.changeStatusTypeToDoneAndInsertExpId(entity.getLeaderQuestExpId(), QuestType.LEADER,
            savedLeaderQuestExp.getLeaderQuestId(), member.getUserId(),
            savedLeaderQuestExp.getMonth(), savedLeaderQuestExp.getWeek());

        if (notificationYn) {
            memberExpService.addGivenExp(member.getUserId(), entity.getExp());
        }

        // 뱃지 조건 충족 시 획득
        badgeAuto.updateBadge(member.getUserId(), YEAR);

        return TransformResult.of(savedLeaderQuestExp, notificationYn);
    }

    /**
     * 스프레드 시트 데이터로 JobQuestExpEntity 수정 메서드
     * @return : JobQuestExpEntity
     */
    private LeaderQuestExpEntity makeLeaderQuestExpEntityBySheet(List rows, Integer sheetRow, Optional<LeaderQuestExpEntity> entityOptional) {

        // questName으로 leaderQuest 식별
        Integer givenExp = TypeConversionUtil.toFlatToInteger(rows.get(6).toString());
        Integer month = TypeConversionUtil.toInteger(rows.get(0).toString());
        Integer week = TypeConversionUtil.toInteger(rows.get(1).toString());
        String employeeNumber = rows.get(2).toString();
        String questName = rows.get(4).toString();

        LeaderQuestEntity leaderQuestEntity = leaderQuestRepository.findByQuestName(questName)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 퀘스트명입니다"));

        LeaderQuestExpEntityBuilder entityBuilder = LeaderQuestExpEntity.builder()
            .leaderQuestId(leaderQuestEntity.getLeaderQuestId())
            .month(month)
            .week(week)
            .employeeNumber(employeeNumber)
            .questName(questName)
            .content(rows.get(5).toString())
            .exp(givenExp)
            .notes(rows.get(7).toString())
            .sheetRow(sheetRow);

        entityOptional.ifPresent(existMember -> entityBuilder.leaderQuestExpId(existMember.getLeaderQuestExpId()));

        return entityBuilder.build();
    }


    private boolean isNotificationYn(Optional<LeaderQuestExpEntity> entityOptional, Integer givenExp) {
        boolean notificationYn = entityOptional.isEmpty() && givenExp != null;
        if (entityOptional.isPresent() && !Objects.equals(entityOptional.get().getExp(), givenExp)) {
            notificationYn = true;
        }
        return notificationYn;
    }
}
