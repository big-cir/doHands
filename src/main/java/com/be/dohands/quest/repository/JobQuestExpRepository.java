package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.JobQuestExp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobQuestExpRepository extends JpaRepository<JobQuestExp, Long> {

    @Query("SELECT qd FROM JobQuestExp qd JOIN JobQuest jq ON qd.jobQuestId = jq.jobQuestId WHERE jq.department = :department")
    List<JobQuestExp> findJobQuestDetailsByDepartment(@Param("department") String department);
}
