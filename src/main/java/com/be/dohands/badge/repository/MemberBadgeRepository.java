package com.be.dohands.badge.repository;

import com.be.dohands.badge.MemberBadge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {

    List<MemberBadge> findMemberBadgesByUserId(Long userId);
}
