package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.LeaderQuestExp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderQuestExpRepository extends JpaRepository<LeaderQuestExp, Long> {

    List<LeaderQuestExp> findLeaderQuestExpsByEmployeeNumber(String employeeNumber);
}
