package com.be.dohands.quest.dto;


import java.time.LocalDateTime;

public record QuestRecentDto(String questType, LocalDateTime createdAt, int exp) {
}
