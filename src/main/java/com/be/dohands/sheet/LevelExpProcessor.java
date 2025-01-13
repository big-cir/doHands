package com.be.dohands.sheet;


import com.be.dohands.level.LevelExp;
import com.be.dohands.level.LevelExp.LevelExpBuilder;
import com.be.dohands.level.repository.LevelExpRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LevelExpProcessor{

    private final LevelExpRepository levelExpRepository;

    private static final List<String> jobGroupList = List.of("현장", "관리", "성장전략", "기술");


    /**
     * 시트 읽어와서 디비 연동하는 메서드
     * 케이스마다 payload 내부 데이터 및 연동 엔티티 개수 등이 달라 케이스별 구현
     * appscript 기반 동작
     */
    public void readSheetAndUpdateDb(Map<String, Object> payload){

        List<Map<String, Object>> rowList = (List<Map<String, Object>>) payload.get("data");

        for (Map<String, Object> row : rowList) {
            List<Object> rowData = (List<Object>) row.get("rowData");               // rowData 가져오기
            transformRowAndSave(rowData);   // 변환
        }
    }

    private void transformRowAndSave(List<Object> rows) {

        for (int i=0;i<4;i++) {
            String name = rows.get(i * 3).toString();
            Integer exp = TypeConversionUtil.toInteger(rows.get(i * 3 + 1));
            String jobGroup = jobGroupList.get(i);

            if (name.isEmpty()) {
                continue;
            }

            Optional<LevelExp> levelExpOptional = levelExpRepository.findByNameAndExpAndJobGroup(name, exp, jobGroup);

            LevelExpBuilder levelExpBuilder = LevelExp.builder()
                .jobGroup(jobGroup)
                .name(name)
                .exp(exp);

            levelExpOptional.ifPresent(existed -> levelExpBuilder.levelExpId(existed.getLevelExpId()));
            LevelExp levelExp = levelExpBuilder.build();

            boolean saveYn = levelExpOptional.map(existed -> existed.isUpdated(levelExp))
                .orElse(true);

            if (saveYn) {
                saveEntity(levelExp);
            }
        }
    }

    private void saveEntity(LevelExp entity) {
        levelExpRepository.save(entity);
    }

}
