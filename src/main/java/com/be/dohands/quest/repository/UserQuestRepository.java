package com.be.dohands.quest.repository;

import com.be.dohands.quest.entity.UserQuestEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuestRepository extends JpaRepository<UserQuestEntity, Long> {

    List<UserQuestEntity> findAllByUserId(Long userId);
}
