package com.be.dohands.quest.service;

import com.be.dohands.quest.dto.QuestRecentDto;
import com.be.dohands.quest.entity.JobQuestExpEntity;
import com.be.dohands.quest.repository.JobQuestExpQueryRepository;
import com.be.dohands.member.dto.CursorResult;
import com.be.dohands.quest.repository.JobQuestExpRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestExpService {

    private final JobQuestExpQueryRepository jobQuestExpQueryRepository;
    private final JobQuestExpRepository jobQuestExpRepository;

    public CursorResult<JobQuestExpEntity> findJobQuestDetailByIds(List<Long> jobQuestIds, String cursor, int size) {
        return jobQuestExpQueryRepository.findJobQuestDetailByIds(jobQuestIds, cursor, size);
    }

    public QuestRecentDto findAllMostRecent(List<Long> jobQuests) {
        JobQuestExpEntity jobQuestExp = jobQuestExpRepository.findTopByJobQuestIdInOrderByCreatedAtDesc(jobQuests)
                .orElse(null);

        if (jobQuestExp == null) return null;
        return new QuestRecentDto("job", jobQuestExp.getCreatedAt(), jobQuestExp.getExp());
    }
}
