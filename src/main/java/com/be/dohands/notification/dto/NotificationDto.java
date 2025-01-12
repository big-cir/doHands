package com.be.dohands.notification.dto;

import com.be.dohands.notification.data.NotificationType;
import lombok.Builder;

@Builder
public record NotificationDto(Long userId, Integer exp, NotificationType notificationType) {
}
