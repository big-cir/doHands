package com.be.dohands.quest.dto;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class QuestListResponseDTO {

    @Builder.Default
    List<Quest> leaderQuestDetailList = new ArrayList<>();

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Quest {
        private Long questId;
        private String questName;
        private Integer month;
        private Integer week;
        private QuestType questType;
        private StatusType statusType;
    }
}
