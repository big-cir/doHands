package com.be.dohands.quest.service;

import com.be.dohands.quest.entity.JobQuestExpEntity;
import com.be.dohands.quest.repository.JobQuestExpQueryRepository;
import com.be.dohands.member.dto.CursorResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestExpService {

    private final JobQuestExpQueryRepository jobQuestExpQueryRepository;

    public CursorResult<JobQuestExpEntity> findJobQuestDetailByIds(List<Long> jobQuestIds, String cursor, int size) {
        return jobQuestExpQueryRepository.findJobQuestDetailByIds(jobQuestIds, cursor, size);
    }
}
