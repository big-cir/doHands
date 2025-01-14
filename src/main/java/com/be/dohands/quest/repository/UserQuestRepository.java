package com.be.dohands.quest.repository;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import com.be.dohands.quest.dto.QuestCountInfo;
import com.be.dohands.quest.entity.UserQuestEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserQuestRepository extends JpaRepository<UserQuestEntity, Long> {

    List<UserQuestEntity> findAllByUserId(Long userId);

    @Query("select uq.userQuestId "
        + "from UserQuestEntity uq "
        + "where uq.questType = :questType and uq.questId = :questId")
    Long findByQuestTypeAndQuestId(QuestType questType, Long questId);

    Optional<UserQuestEntity> findByUserQuestId(Long userQuestId);

    @Query(value = "select new com.be.dohands.quest.dto.QuestCountInfo(qs.month, qs.week , uq.questType, uq.statusType, cast(coalesce(count(uq.userQuestId),0) as int)) "
        + "from UserQuestEntity uq "
        + "left join QuestScheduleEntity qs on uq.questScheduleId = qs.questScheduleId "
        + "where uq.userId = :userId and uq.questType = :questType and qs.month = :month "
        + "group by qs.month, qs.week, uq.questType, uq.statusType "
        + "order by uq.statusType ")
    List<QuestCountInfo> totalQuestsByQuestTypeAndMonth (@Param("userId") Long userId, @Param("questType") QuestType questType, @Param("month") Integer month);

    @Query(value = "select new com.be.dohands.quest.dto.QuestCountInfo(qs.month, qs.week , uq.questType, uq.statusType, cast(coalesce(count(uq.userQuestId),0) as int)) "
        + "from UserQuestEntity uq "
        + "left join QuestScheduleEntity qs on uq.questScheduleId = qs.questScheduleId "
        + "where uq.userId = :userId and uq.questType = :questType and qs.week = :week "
        + "group by qs.month, qs.week, uq.questType, uq.statusType "
        + "order by uq.statusType ")
    List<QuestCountInfo> totalQuestsByQuestTypeAndWeek (@Param("userId") Long userId, @Param("questType") QuestType questType, @Param("week") Integer week);

    @Query(value = "select uq from UserQuestEntity uq "
        + "where uq.questScheduleId = :questScheduleId and uq.userId = :userId")
    UserQuestEntity findByQuestScheduleIdAndUserId(@Param("questScheduleId") Long questScheduleId, @Param("userId") Long userId);

    Optional<UserQuestEntity> findByQuestTypeAndQuestIdAndUserIdAndMonthAndWeek(QuestType questType, Long questId, Long userId, Integer month, Integer week);

    List<UserQuestEntity> findByQuestScheduleId(@Param("questScheduleId") Long questScheduleId);

    @Query(value = "select cast(coalesce(count(uq.userQuestId),0) as int) "
        + "from UserQuestEntity uq "
        + "where uq.userId = :userId and uq.statusType = :statusType")
    Integer countIdsByUserIdAndStatus(@Param("userId") Long userId, @Param("statusType") StatusType statusType);
}
