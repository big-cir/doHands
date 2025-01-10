package com.be.dohands.member.dto;

import java.util.List;

public record MultiCursorResult<T> (List<T> items, String cursor, boolean hasNext) {
}
