package com.be.dohands.quest.dto;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestCountInfo {

    private Integer month;

    private Integer week;

    private QuestType questType;

    private StatusType status;

    private Integer count;

    public QuestCountInfo(Integer month, Integer week, QuestType questType, StatusType status, Integer count) {
        this.month = month;
        this.week = week;
        this.questType = questType;
        this.status = status;
        this.count = count;
    }


}
