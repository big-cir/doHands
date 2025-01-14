package com.be.dohands.member.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MemberInfoResponseDTO {
    private String employeeNumber; // 사원번호
    private String name; // 이름
    private String department; //  소속
    private String characterType; // 현재 적용중인 캐릭터
    private String levelName; // 레벨명 (현장 1)
    private LocalDate hireDate; // 입사일
    private String jobCategory; // 직군
    private Long skinId; // 현재 적용중인 스킨
    @Default
    private Integer unreadArticles = 0; // 안읽은 게시글 수
}