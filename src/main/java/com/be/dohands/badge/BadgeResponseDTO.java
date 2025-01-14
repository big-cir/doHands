package com.be.dohands.badge;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BadgeResponseDTO {
    @Builder.Default
    private List<BadgeInfo> badgeInfoList = new ArrayList<>();

    @Builder
    @Getter
    @AllArgsConstructor
    public static class BadgeInfo{
        private Long badgeId;
        private String name;
        private String condition;
        private Boolean acheived;
    }
}
