package com.be.dohands.level.repository;

import com.be.dohands.level.LevelExp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelExpRepository extends JpaRepository<LevelExp, Long> {

    Optional<LevelExp> findLevelExpByName(String name);

    Optional<LevelExp> findByNameAndExpAndJobGroup(String name, Integer exp, String jobGroup);
}
