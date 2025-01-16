package com.be.dohands.member.dto;

import com.be.dohands.quest.data.QuestType;
import com.be.dohands.quest.data.StatusType;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestsInProgressResponseDTO {
    @Builder.Default
    List<QuestInProgress> questsInProgressList = new ArrayList<>();

    @Getter
    public static class QuestInProgress{
       private String questName;
       private QuestType questType;
       private StatusType statusType;

        public QuestInProgress(String questName, QuestType questType, StatusType statusType) {
            this.questName = questName;
            this.questType = questType;
            this.statusType = statusType;
        }
    }
}
