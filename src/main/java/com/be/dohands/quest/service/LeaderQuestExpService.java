package com.be.dohands.quest.service;

import com.be.dohands.quest.dto.QuestRecentDto;
import com.be.dohands.quest.entity.LeaderQuestExpEntity;
import com.be.dohands.quest.repository.LeaderQuestExpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaderQuestExpService {

    private final LeaderQuestExpRepository leaderQuestExpRepository;

    @Transactional(readOnly = true)
    public QuestRecentDto findAllMostRecent(String employeeNumber) {
        LeaderQuestExpEntity leaderQuestExp = leaderQuestExpRepository.findTopByEmployeeNumberOrderByCreatedAtDesc(
                employeeNumber).orElse(null);

        if (leaderQuestExp == null) return null;
        return new QuestRecentDto("leader", leaderQuestExp.getCreatedAt(), leaderQuestExp.getExp());
    }
}
