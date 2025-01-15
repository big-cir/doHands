package com.be.dohands.notification.service;

import static com.be.dohands.notification.data.NotificationType.EXP;

import com.be.dohands.notification.data.NotificationType;
import com.be.dohands.notification.dto.NotificationDto;
import com.be.dohands.notification.entity.FcmTokenEntity;
import com.be.dohands.notification.repository.FcmTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveToken(String token, Long userId) {
        FcmTokenEntity fcmTokenEntity = fcmTokenRepository.findByUserId(userId);
        if (fcmTokenEntity != null) {
            fcmTokenEntity.updateToken(token);
            fcmTokenRepository.save(fcmTokenEntity);
        } else {
            fcmTokenRepository.save(new FcmTokenEntity(token, userId));
        }
    }

    public void send(NotificationDto notificationDto) {
        NotificationType type = notificationDto.notificationType();
        if (type.equals(EXP)) {
            getExpNotification(notificationDto.userId(), type.getData());
        } else {
            createArticleNotification(type.getData());
        }
    }

    private void getExpNotification(Long userId, Map<String, String> data) {
        String token = fcmTokenRepository.findByUserId(userId).getToken();
        getFirebaseInstance().sendAsync(
                Message.builder()
                        .setToken(token)
                        .putAllData(data)
                        .build()
        );
    }

    private void createArticleNotification(Map<String, String> data) {
        List<String> tokens = fcmTokenRepository.findAll().stream()
                .map(FcmTokenEntity::getToken)
                .toList();

        getFirebaseInstance().sendMulticastAsync(
                MulticastMessage.builder()
                        .addAllTokens(tokens)
                        .putAllData(data)
                        .build()
        );
    }

    private FirebaseMessaging getFirebaseInstance() {
        return FirebaseMessaging.getInstance();
    }
}
