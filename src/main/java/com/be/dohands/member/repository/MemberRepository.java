package com.be.dohands.member.repository;

import com.be.dohands.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginId(String loginId);

    Optional<Member> findByLoginId(String loginId);

    Member findByName(String name);

    Optional<Member> findBySheetRow(Integer sheetRow);

    List<Member> findMembersByDepartmentAndJobGroup(String department, String jobGroup);

    List<Member> findMembersByDepartment(String department);

    Optional<Member> findByEmployeeNumber(String employeeNumber);

    Optional<Member> findByDepartment(String department);
}
