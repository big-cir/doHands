package com.be.dohands.member.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestsInProgressRequestDTO {
    private Integer year;
    private Integer month;
    private Integer week;
    private String department;
}
