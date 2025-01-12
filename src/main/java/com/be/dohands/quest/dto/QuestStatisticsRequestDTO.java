package com.be.dohands.quest.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestStatisticsRequestDTO {
    private Integer year;
    private Integer month;
}
