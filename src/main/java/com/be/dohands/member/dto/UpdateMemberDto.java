package com.be.dohands.member.dto;

import java.time.LocalDate;

public record UpdateMemberDto(
        String name, String loginId, String employeeNumber, String department, Long levelId, LocalDate hireDate) {
}
