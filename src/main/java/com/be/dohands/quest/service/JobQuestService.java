package com.be.dohands.quest.service;

import com.be.dohands.quest.entity.JobQuest;
import com.be.dohands.quest.entity.JobQuestExp;
import com.be.dohands.quest.repository.JobQuestRepository;
import com.be.dohands.member.dto.CursorResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestService {

    private final JobQuestRepository jobQuestRepository;

    private final JobQuestExpService jobQuestExpService;

    public CursorResult<JobQuestExp> findJobQuestExpByDepartment(String department, String cursor, int size) {
        List<Long> ids = jobQuestRepository.findJobQuestsByDepartment(department)
                .stream()
                .map(JobQuest::getJobQuestId)
                .toList();

        return jobQuestExpService.findJobQuestDetailByIds(ids, cursor, size);
    }
}
