package com.be.dohands.member.dto;

import java.time.LocalDateTime;

public record QuestExpDto(Long itemId, String questName, int exp, String itemType, LocalDateTime createdAt) {
}
