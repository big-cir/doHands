package com.be.dohands.sheet;


import com.be.dohands.tf.TfExp;
import com.be.dohands.tf.TfExp.TfExpBuilder;
import com.be.dohands.tf.repository.TfExpRepository;
import java.util.List;
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
        log.info("TFexp = " + tfExp + "\n notificationYN == " + notificationYn);
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
