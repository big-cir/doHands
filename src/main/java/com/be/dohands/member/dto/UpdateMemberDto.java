package com.be.dohands.member.dto;

public record UpdateMemberDto(
        String name, String loginId, String password, String department, String level, String jobGroup, String jobCategory) {
}
