package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.JobQuestEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobQuestRepository extends JpaRepository<JobQuestEntity, Long> {

    List<JobQuestEntity> findJobQuestsByDepartment(String department);

    Optional<JobQuestEntity> findByJobQuestId(Long jobQuestId);

    Optional<JobQuestEntity> findBySheetRow(Integer sheetRow);

    List<JobQuestEntity> findByYear(Integer year);

    Optional<JobQuestEntity> findJobQuestEntityByDepartmentAndJobGroupAndPeriod(String department, String jobGroup,
        String period);

    List<JobQuestEntity> findJobQuestsByDepartmentAndJobGroup(String department, String jobGroup);
}
