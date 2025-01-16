package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.LeaderQuestEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaderQuestRepository extends JpaRepository<LeaderQuestEntity, Long> {

    Optional<LeaderQuestEntity> findByLeaderQuestId(Long leaderQuestId);

    List<LeaderQuestEntity> findByYear(Integer year);

    Optional<LeaderQuestEntity> findBySheetRow(Integer sheetRow);

    Optional<LeaderQuestEntity> findByQuestName(String questName);

    Optional<LeaderQuestEntity> findBySheetRowAndDepartment(int sheetRow, String department);

    Optional<LeaderQuestEntity> findByQuestNameAndDepartment(String questName, String department);
}
