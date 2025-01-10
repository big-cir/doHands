package com.be.dohands.member.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class CursorResult<T> {
    private List<T> items;
    private String cursor;
    private boolean isLast;

    public CursorResult(List<T> items, String cursor) {
        this.items = items;
        this.cursor = cursor;
        this.isLast = false;
    }

    public void updateLast() {
        this.isLast = true;
    }

    public boolean isLast() {
        return isLast;
    }
}
