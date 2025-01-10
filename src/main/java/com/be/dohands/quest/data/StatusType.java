package com.be.dohands.quest.data;

import lombok.Getter;

@Getter
public enum StatusType {
    NOT_STARTED("진행예정"),
    IN_PROGRESS("진행중"),
    DONE("완료"),
    FAIL("실패");

    private final String statusType;

    StatusType(String statusType){
        this.statusType = statusType;
    }
}
