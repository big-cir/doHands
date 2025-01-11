package com.be.dohands.notification.repository;

import com.be.dohands.notification.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmTokenEntity, Long> {

    FcmTokenEntity findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
