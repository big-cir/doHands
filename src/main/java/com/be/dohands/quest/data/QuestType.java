package com.be.dohands.quest.data;

import lombok.Getter;

@Getter
public enum QuestType {
    LEADER("리더부여 퀘스트"),
    JOB("직무별 퀘스트");

    private final String questType;

    QuestType(String questType){
        this.questType = questType;
    }
}
