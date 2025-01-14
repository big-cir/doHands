package com.be.dohands.tf.service;

import com.be.dohands.quest.dto.QuestRecentDto;
import com.be.dohands.tf.TfExp;
import com.be.dohands.tf.repository.TfExpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TfExpService {

    private final TfExpRepository tfExpRepository;

    @Transactional(readOnly = true)
    public QuestRecentDto findAllMostRecent(String employeeNumber) {
        TfExp tfExp = tfExpRepository.findTopByEmployeeNumberOrderByCreatedAtDesc(employeeNumber).orElse(null);

        if (tfExp == null) return null;
        return new QuestRecentDto("tf", tfExp.getCreatedAt(), tfExp.getExp());
    }
}
