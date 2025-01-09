package com.be.dohands.member.dto;

import com.be.dohands.member.Member;
import java.time.LocalDate;

public record CreateMemberDto(
        String name, String loginId, String employeeNumber, String department, Long levelId, LocalDate hireDate) {

    public Member toMember() {
        return new Member(name, loginId, employeeNumber, department, levelId, hireDate);
    }
}
