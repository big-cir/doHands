package com.be.dohands.sheet;


import com.be.dohands.badge.BadgeAuto;
import com.be.dohands.member.Member;
import com.be.dohands.member.repository.MemberRepository;
import com.be.dohands.member.service.MemberExpService;
import com.be.dohands.tf.TfExp;
import com.be.dohands.tf.TfExp.TfExpBuilder;
import com.be.dohands.tf.repository.TfExpRepository;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TfExpProcessor extends SheetProcessor<TfExp>{

    private final TfExpRepository tfExpRepository;
    private final MemberRepository memberRepository;

    private final MemberExpService memberExpService;
    private final BadgeAuto badgeAuto;

    // 퀘스트 생성 시 연도 : 시트 미기재 속성이여서 시스템에서 직접 지정
    private final Integer YEAR = Year.now(ZoneId.of("Asia/Seoul")).getValue();

    @Override
    protected TransformResult<TfExp> transformRow(List<Object> rows, Integer sheetRow) {

        Optional<TfExp> tfExpOptional = tfExpRepository.findBySheetRow(sheetRow);
        Integer givenExp = TypeConversionUtil.toInteger(rows.get(5));

        TfExpBuilder tfExpBuilder = TfExp.builder()
            .employeeNumber(rows.get(2).toString())
            .projectName(rows.get(4).toString())
            .exp(givenExp)
            .notes(rows.get(6).toString())
            .month(TypeConversionUtil.toInteger(rows.get(0)))
            .date(TypeConversionUtil.toInteger(rows.get(1)))
            .sheetRow(sheetRow);

        tfExpOptional.ifPresent(existTfExp -> tfExpBuilder.tfExpId(existTfExp.getTfExpId()));
        TfExp tfExp = tfExpBuilder.build();
        boolean notificationYn = isNotificationYn(tfExpOptional, givenExp);

        TfExp savedTfExp = tfExpRepository.save(tfExp);

        Member member = memberRepository.findByEmployeeNumber(savedTfExp.getEmployeeNumber())
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사원번호"));

        if (notificationYn) {
            memberExpService.addGivenExp(member.getUserId(), tfExp.getExp());
        }

        // 뱃지 조건 충족 시 획득
        badgeAuto.updateBadge(member.getUserId(), YEAR);

        return TransformResult.of(tfExp, notificationYn);
    }

    @Override
    protected TfExp saveEntity(TfExp entity) {
        return tfExpRepository.save(entity);
    }

    private boolean isNotificationYn(Optional<TfExp> tfExpOptional, Integer givenExp) {
        boolean notificationYn = tfExpOptional.isEmpty() && givenExp != null;
        if (tfExpOptional.isPresent() && !Objects.equals(tfExpOptional.get().getExp(), givenExp)) {
            notificationYn = true;
        }
        return notificationYn;
    }
}
