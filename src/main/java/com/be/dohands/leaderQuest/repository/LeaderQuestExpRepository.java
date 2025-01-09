package com.be.dohands.leaderQuest.repository;

import com.be.dohands.leaderQuest.LeaderQuestExp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderQuestExpRepository extends JpaRepository<LeaderQuestExp, Long> {

    List<LeaderQuestExp> findLeaderQuestExpsByEmployeeNumber(String employeeNumber);
}
