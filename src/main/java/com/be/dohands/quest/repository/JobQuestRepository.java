package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.JobQuestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobQuestRepository extends JpaRepository<JobQuestEntity, Long> {

    List<JobQuestEntity> findJobQuestsByDepartment(String department);
}
