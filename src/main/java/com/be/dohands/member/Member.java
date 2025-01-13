package com.be.dohands.member;

import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String loginId;

    private String password;

    private String employeeNumber;

    private String department;

    private String name;

    private String characterType;

    private Long levelId;

    private LocalDate hireDate;

    private Long skinId;

    @Enumerated(EnumType.STRING)
    private final Role role = Role.ROLE_USER;

    private String jobGroup;

    private String jobCategory;

    private Integer sheetRow;

    public Member(String name, String loginId, String employeeNumber, String department, Long levelId, LocalDate hireDate, String jobGroup, String jobCategory) {
        this.name = name;
        this.loginId = loginId;
        this.password = "1234";
        this.employeeNumber = employeeNumber;
        this.department = department;
        this.levelId = levelId;
        this.hireDate = hireDate;
        this.characterType = "default";
        this.jobGroup = jobGroup;
        this.jobCategory = jobCategory;
    }

    public void updateMember(String name, String loginId, String employeeNumber, String department, Long levelId, LocalDate hireDate, String jobGroup) {
        this.name = name;
        this.loginId = loginId;
        this.employeeNumber = employeeNumber;
        this.department = department;
        this.levelId = levelId;
        this.hireDate = hireDate;
        this.jobGroup = jobGroup;
    }

    public void updatePassword(String password) {
        if (password != null && !password.isEmpty() && !password.isBlank()) {
            this.password = password;
        }
    }

    public void updateCharacter(String characterType) {
        if (characterType != null && !characterType.isEmpty() && !characterType.isBlank()) {
            this.characterType = characterType;
        }
    }
}
