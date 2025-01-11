package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.LeaderQuestExpEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderQuestExpRepository extends JpaRepository<LeaderQuestExpEntity, Long> {

    List<LeaderQuestExpEntity> findLeaderQuestExpsByEmployeeNumber(String employeeNumber);

    Optional<LeaderQuestExpEntity> findByLeaderQuestExpId(Long questExpId);
}
