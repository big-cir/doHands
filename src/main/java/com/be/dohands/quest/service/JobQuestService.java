package com.be.dohands.quest.service;

import com.be.dohands.quest.dto.QuestRecentDto;
import com.be.dohands.quest.entity.JobQuestEntity;
import com.be.dohands.quest.entity.JobQuestExpEntity;
import com.be.dohands.quest.repository.JobQuestRepository;
import com.be.dohands.member.dto.CursorResult;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestService {

    private final JobQuestRepository jobQuestRepository;

    private final JobQuestExpService jobQuestExpService;

    public CursorResult<JobQuestExpEntity> findJobQuestExpByDepartment(String department, String cursor, int size) {
        List<Long> ids = jobQuestRepository.findJobQuestsByDepartment(department)
                .stream()
                .map(JobQuestEntity::getJobQuestId)
                .toList();

        return jobQuestExpService.findJobQuestDetailByIds(ids, cursor, size);
    }

    public QuestRecentDto findAllMostRecent(String department) {
        List<Long> ids = jobQuestRepository.findJobQuestsByDepartment(department).stream()
                .map(JobQuestEntity::getJobQuestId)
                .collect(Collectors.toList());
        return jobQuestExpService.findAllMostRecent(ids);
    }
}
