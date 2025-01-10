package com.be.dohands.jobQuest.repository;

import com.be.dohands.jobQuest.JobQuest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobQuestRepository extends JpaRepository<JobQuest, Long> {

    List<JobQuest> findJobQuestsByDepartment(String department);
}
