package com.be.dohands.level.repository;

import com.be.dohands.level.LevelExp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LevelExpRepository extends JpaRepository<LevelExp, Long> {

    Optional<LevelExp> findLevelExpByName(String name);

    Optional<LevelExp> findByNameAndExpAndJobGroup(String name, Integer exp, String jobGroup);

    List<LevelExp> findLevelExpsByJobGroupStartingWith(String jobCategory);

    Optional<LevelExp> findFirstByJobGroupStartingWithAndExpGreaterThan(String jobCategory, Integer exp);

    @Query(value = "select cast(coalesce(count(l.levelExpId),0) as int ) from LevelExp l where l.jobGroup = :jobCategory")
    Integer countLevelIdsByJobCategory(@Param("jobCategory") String jobCategory);

    LevelExp findByLevelExpId(Long levelId);

    LevelExp findByName(String nextLevelName);
}
