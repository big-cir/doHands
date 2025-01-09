package com.be.dohands.quest.service;

import com.be.dohands.quest.repository.JobQuestExpRepository;
import com.be.dohands.quest.repository.JobQuestRepository;
import com.be.dohands.member.dto.QuestExpDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobQuestService {

    private final JobQuestRepository jobQuestRepository;
    private final JobQuestExpRepository jobQuestExpRepository;

    public List<QuestExpDto> findJobQuestExpByDepartment(String department) {
        return jobQuestExpRepository.findJobQuestDetailsByDepartment(department)
                .stream()
                .map(questDetail -> new QuestExpDto(department, questDetail.getExp()))
                .toList();
    }
}
