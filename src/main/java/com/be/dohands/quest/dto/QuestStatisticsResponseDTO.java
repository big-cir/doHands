package com.be.dohands.quest.dto;

import com.be.dohands.quest.data.QuestType;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class QuestStatisticsResponseDTO {

    @Builder.Default
    List<QuestInfo> questStatistics = new ArrayList<>();

    public static class QuestInfo {

        private Integer month;
        private Integer week;
        private QuestType questType;

        @Setter
        private int total;

        @Setter
        private int done;

        public QuestInfo(Integer month, Integer week, QuestType questType, int total, int done) {
            this.month = month;
            this.week = week;
            this.questType = questType;
            this.total = total;
            this.done = done;
        }
    }

}
