package com.be.dohands.member.dto;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record QuestResult(List<QuestExpDto> items, CursorResult<?> cursorResult) {
    public static <T> QuestResult processQuestType(
            Supplier<CursorResult<T>> fetchData,
            Function<T, QuestExpDto> mapper
    ) {
        CursorResult<T> cursorResult = fetchData.get();
        List<QuestExpDto> items = cursorResult.getItems()
                .stream()
                .map(mapper)
                .toList();

        return new QuestResult(items, cursorResult);
    }
}

