package com.be.dohands.quest.dto;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JobQuestDetailResponseDTO {
    private String questName;
    private QuestType questType;
    private StatusType statusType;
    private Integer month;
    private Integer week;
    private Integer maxExp;
    private Integer medianExp;
    private Float maxStandard;
    private Float medianStandard;
    private Float productivity;
    private Integer exp;
    private String notes;
}