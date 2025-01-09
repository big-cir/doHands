package com.be.dohands.member.repository;

import com.be.dohands.member.MemberExp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberExpRepository extends JpaRepository<MemberExp, Long> {

    Optional<MemberExp> findByUserId(Long userId);
}
