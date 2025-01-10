package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.JobQuest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobQuestRepository extends JpaRepository<JobQuest, Long> {

    List<JobQuest> findJobQuestsByDepartment(String department);
}
