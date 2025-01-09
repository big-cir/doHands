package com.be.dohands.jobQuest.service;

import com.be.dohands.jobQuest.JobQuest;
import com.be.dohands.jobQuest.JobQuestDetail;
import com.be.dohands.jobQuest.repository.JobQuestRepository;
import com.be.dohands.member.dto.CursorResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestService {

    private final JobQuestRepository jobQuestRepository;

    private final JobQuestDetailService jobQuestDetailService;

    public CursorResult<JobQuestDetail> findJobQuestExpByDepartment(String department, String cursor, int size) {
        List<Long> ids = jobQuestRepository.findJobQuestsByDepartment(department)
                .stream()
                .map(JobQuest::getJobQuestId)
                .toList();

        return jobQuestDetailService.findJobQuestDetailByIds(ids, cursor, size);
    }
}
