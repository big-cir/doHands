package com.be.dohands.jobQuest.repository;

import com.be.dohands.jobQuest.JobQuestDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobQuestDetailRepository extends JpaRepository<JobQuestDetail, Long> {

    @Query("SELECT qd FROM JobQuestDetail qd JOIN JobQuest jq ON qd.jobQuestId = jq.jobQuestId WHERE jq.department = :department")
    List<JobQuestDetail> findJobQuestDetailsByDepartment(@Param("department") String department);
}
