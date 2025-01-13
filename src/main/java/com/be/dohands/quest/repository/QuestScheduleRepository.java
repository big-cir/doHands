package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.QuestScheduleEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestScheduleRepository extends JpaRepository<QuestScheduleEntity, Long> {

    Optional<QuestScheduleEntity> findByQuestScheduleId(Long questScheduleId);

    @Query(value = "select qs "
        + "from QuestScheduleEntity qs "
        + "where qs.department = : department and qs.year = :year and qs.month = :month ")
    List<QuestScheduleEntity> findByDepartmentAndMonth(@Param("department") String department,
        @Param("year") Integer year, @Param("month") Integer month);

    @Query(value = "select qs "
        + "from QuestScheduleEntity qs "
        + "where qs.department = : department and qs.year = :year and qs.month = :month ")
    List<QuestScheduleEntity> findByDepartmentAndWeek(@Param("department") String department,
        @Param("year") Integer year, @Param("week") Integer week);
}
