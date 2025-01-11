package com.be.dohands.quest.dto;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LeaderQuestDetailResponseDTO {
    private String questName;
    private QuestType questType;
    private StatusType statusType;
    private Integer month;
    private Integer week;
    private Integer maxExp;
    private Integer medianExp;
    private String maxStandard;
    private String medianStandard;
    private Integer exp;
    private String notes;
}