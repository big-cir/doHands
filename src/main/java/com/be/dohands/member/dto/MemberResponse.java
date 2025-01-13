package com.be.dohands.member.dto;

import com.be.dohands.member.Member;
import com.be.dohands.member.Role;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MemberResponse {
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long userId;
    private String loginId;
    private String password;
    private String employeeNumber;
    private String department;
    private String name;
    private String characterType;
    private String level;
    private LocalDate hireDate;
    private Long skinId;
    private Role role;

    public MemberResponse(Member member, String level) {
        this.createdAt = member.getCreatedAt();
        this.modifiedAt = member.getModifiedAt();
        this.userId = member.getUserId();
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.employeeNumber = member.getEmployeeNumber();
        this.department = member.getDepartment();
        this.name = member.getName();
        this.characterType = member.getCharacterType();
        this.level = level;
        this.hireDate = member.getHireDate();
        this.skinId = member.getSkinId();
        this.role = member.getRole();
    }
}