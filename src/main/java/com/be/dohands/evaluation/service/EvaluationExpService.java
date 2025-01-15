package com.be.dohands.evaluation.service;

import com.be.dohands.evaluation.EvaluationExp;
import com.be.dohands.evaluation.repository.EvaluationExpRepository;
import com.be.dohands.quest.dto.QuestRecentDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationExpService {

    private final EvaluationExpRepository evaluationExpRepository;

    @Transactional(readOnly = true)
    public QuestRecentDto findAllMostRecent(String employeeNumber) {
        EvaluationExp evaluationExp = evaluationExpRepository.findTopByEmployeeNumberOrderByCreatedAtDesc(
                employeeNumber).orElse(null);

        if (evaluationExp == null) return null;
        return new QuestRecentDto("evaluation", LocalDateTime.now(), evaluationExp.getExp());
    }
}
