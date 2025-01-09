package com.be.dohands.jobQuest.service;

import com.be.dohands.jobQuest.JobQuest;
import com.be.dohands.jobQuest.repository.JobQuestDetailRepository;
import com.be.dohands.jobQuest.repository.JobQuestRepository;
import com.be.dohands.member.dto.QuestExpDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestService {

    private final JobQuestRepository jobQuestRepository;
    private final JobQuestDetailRepository jobQuestDetailRepository;

    public List<QuestExpDto> findJobQuestExpByDepartment(String department) {
        return jobQuestDetailRepository.findJobQuestDetailsByDepartment(department)
                .stream()
                .map(questDetail -> new QuestExpDto(department, questDetail.getExp()))
                .toList();
    }
}
