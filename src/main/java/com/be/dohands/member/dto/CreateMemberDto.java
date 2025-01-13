package com.be.dohands.member.dto;

import com.be.dohands.member.Member;
import java.time.LocalDate;

public record CreateMemberDto(
        String name, String loginId, String employeeNumber, String department, String level, LocalDate hireDate,
        String jobGroup, String jobCategory) {

    public Member toMember(Long levelId) {
        return new Member(name, loginId, employeeNumber, department, levelId, hireDate, jobGroup, jobCategory);
    }
}