package com.be.dohands.quest.service;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.entity.UserQuestEntity;
import com.be.dohands.quest.repository.UserQuestRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserQuestService {

    private final UserQuestRepository userQuestRepository;

    public UserQuestEntity changeStatusTypeToDoneAndInsertExpId(Long expId, QuestType questType, Long questId, Long userId, Integer month, Integer week) {

        UserQuestEntity userQuest = userQuestRepository.findByQuestTypeAndQuestIdAndUserIdAndMonthAndWeek(
            questType, questId, userId, month, week).orElseThrow(() -> new NoSuchElementException("존재하지 않는 userQuest"));

        userQuest.statusTypeToDone();
        userQuest.giveQuestExpId(expId);
        return userQuestRepository.save(userQuest);
    }

}
