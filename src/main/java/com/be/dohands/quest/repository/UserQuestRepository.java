package com.be.dohands.quest.repository;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.entity.UserQuestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserQuestRepository extends JpaRepository<UserQuestEntity, Long> {

    List<UserQuestEntity> findAllByUserId(Long userId);

    @Query("select uq.userQuestId "
        + "from UserQuestEntity uq "
        + "where uq.questType = :questType and uq.questId = :questId")
    Long findByQuestTypeAndQuestId(QuestType questType, Long questId);
}
