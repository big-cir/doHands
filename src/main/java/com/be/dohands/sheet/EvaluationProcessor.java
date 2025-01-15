package com.be.dohands.sheet;


import com.be.dohands.badge.BadgeAuto;
import com.be.dohands.evaluation.EvaluationExp;
import com.be.dohands.evaluation.EvaluationExp.EvaluationExpBuilder;
import com.be.dohands.evaluation.TermType;
import com.be.dohands.evaluation.repository.EvaluationExpRepository;
import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.member.service.MemberExpService;
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
public class EvaluationProcessor {

    private final EvaluationExpRepository evaluationExpRepository;
    private final MemberRepository memberRepository;

    private final MemberExpService memberExpService;
    private final BadgeAuto badgeAuto;

    // 퀘스트 생성 시 연도 : 시트 미기재 속성이여서 시스템에서 직접 지정
    private final Integer YEAR = Year.now(ZoneId.of("Asia/Seoul")).getValue();


    public List<TransformResult<?>> readDividedSheetAndUpdateDb(Map<String, Object> payload) {
        String sheetName = (String) payload.get("sheetName");
        Map<String, Object> dataMap = (Map<String, Object>) payload.get("data");
        List<Map<String, Object>> data1 = (List<Map<String, Object>>) dataMap.get("data1");
        List<Map<String, Object>> data2 = (List<Map<String, Object>>) dataMap.get("data2");

        log.info("Processing sheet: " + sheetName);
        List<TransformResult<?>> results = new ArrayList<>(); // 알람전송용 리스트

        // 인사평가 하반기
        if (data1 != null) {
            for (Map<String, Object> row : data1) {
                List<Object> rowData = (List<Object>) row.get("rowData");               // rowData 가져오기
                TransformResult transformResult = makeEvaluationExpTransformResult(rowData, TermType.H1);   // 변환
                results.add(transformResult);
            }
        }

        // 인사평가 하반기
        if (data2 != null) {
            for (Map<String, Object> row : data2) {
                List<Object> rowData = (List<Object>) row.get("rowData");               // rowData 가져오기
                TransformResult transformResult = makeEvaluationExpTransformResult(rowData,TermType.H2);   // 변환
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
    private EvaluationExp makeEvaluationExpBySheet(List rows, TermType termType, Optional<EvaluationExp> entityOptional) {

        String employeeNumber = rows.get(0).toString();

        EvaluationExpBuilder entityBuilder = EvaluationExp.builder()
            .employeeNumber(employeeNumber)
            .grade(rows.get(2).toString())
            .exp(TypeConversionUtil.toInteger(rows.get(3).toString()))
            .notes(rows.get(4).toString())
            .year(YEAR)
            .termType(termType);

        entityOptional.ifPresent(existMember -> entityBuilder.evaluationExpId(existMember.getEvaluationExpId()));

        return entityBuilder.build();
    }

    /**
     * JobQuestExpEntity 생성 및 알림 전송 여부 반환 메서드
     * @param : 스프레드 시트 데이터 & 해당하는 JobQuestEntity
     * @return JobQuestExpEntity & 알림 전송 여부
     */
    public TransformResult<EvaluationExp> makeEvaluationExpTransformResult(List rows, TermType termType) {

        String employeeNumber = rows.get(0).toString();
        Optional<EvaluationExp> entityOptional = evaluationExpRepository.findEvaluationExpByEmployeeNumberAndTermTypeAndYear(employeeNumber,termType,YEAR);

        EvaluationExp entity = makeEvaluationExpBySheet(rows, termType, entityOptional);

        boolean notificationYn = isNotificationYn(entityOptional, entity.getExp());

        EvaluationExp savedEvaluationExp = evaluationExpRepository.save(entity);

        Member member = memberRepository.findByEmployeeNumber(savedEvaluationExp.getEmployeeNumber())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원번호"));

        if (notificationYn) {
            memberExpService.addGivenExp(member.getUserId(), entity.getExp());
        }

        // 뱃지 조건 충족 시 획득
        badgeAuto.updateBadge(member.getUserId(), YEAR);

        return TransformResult.of(savedEvaluationExp, notificationYn);
    }

    private boolean isNotificationYn(Optional<EvaluationExp> evaluationExpOptional, Integer givenExp) {
        boolean notificationYn = evaluationExpOptional.isEmpty() && givenExp != null;
        if (evaluationExpOptional.isPresent() && !Objects.equals(evaluationExpOptional.get().getExp(), givenExp)) {
            notificationYn = true;
        }
        return notificationYn;
    }
}
