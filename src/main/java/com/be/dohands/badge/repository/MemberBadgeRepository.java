package com.be.dohands.badge.repository;

import com.be.dohands.badge.MemberBadge;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberBadgeRepository extends JpaRepository<MemberBadge, Long> {

    List<MemberBadge> findMemberBadgesByUserId(Long userId);

    @Query("select mb.badgeId "
        + "from MemberBadge mb "
        + "where mb.userId = :userId")
    List<Long> findMemberBadgeIdsByUserId(@Param("userId") Long userId);
}
