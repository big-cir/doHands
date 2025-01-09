package com.be.dohands.jobQuest.service;

import com.be.dohands.jobQuest.JobQuestDetail;
import com.be.dohands.jobQuest.repository.JobQuestDetailQueryRepository;
import com.be.dohands.member.dto.CursorResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestDetailService {

    private final JobQuestDetailQueryRepository jobQuestDetailQueryRepository;

    public CursorResult<JobQuestDetail> findJobQuestDetailByIds(List<Long> jobQuestIds, String cursor, int size) {
        return jobQuestDetailQueryRepository.findJobQuestDetailByIds(jobQuestIds, cursor, size);
    }
}
