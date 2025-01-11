package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.JobQuestExpEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobQuestExpRepository extends JpaRepository<JobQuestExpEntity, Long> {

    @Query("SELECT qd FROM JobQuestExpEntity qd JOIN JobQuestEntity jq ON qd.jobQuestId = jq.jobQuestId WHERE jq.department = :department")
    List<JobQuestExpEntity> findJobQuestDetailsByDepartment(@Param("department") String department);
    Optional<JobQuestExpEntity> findByJobQuestExpId(Long questExpId);
}
