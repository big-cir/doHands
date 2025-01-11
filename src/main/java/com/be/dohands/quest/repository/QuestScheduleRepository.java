package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.QuestScheduleEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestScheduleRepository extends JpaRepository<QuestScheduleEntity, Long> {

    Optional<QuestScheduleEntity> findByQuestScheduleId(Long questScheduleId);
}
