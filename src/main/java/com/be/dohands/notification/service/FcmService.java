package com.be.dohands.notification.service;

import static com.be.dohands.notification.data.NotificationType.EXP;

import com.be.dohands.notification.data.NotificationType;
import com.be.dohands.notification.dto.NotificationDto;
import com.be.dohands.notification.entity.FcmTokenEntity;
import com.be.dohands.notification.repository.FcmTokenRepository;
import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
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
        FirebaseMessaging fi = getFirebaseInstance();
        fcmTokenRepository.findAll().stream()
                .map(FcmTokenEntity::getToken)
                .forEach(token -> {
                            Message message = Message.builder()
                                    .setToken(token)
                                    .setAndroidConfig(
                                            AndroidConfig.builder()
                                                    .setNotification(
                                                            AndroidNotification.builder()
                                                                    .setTitle(data.get("title"))
                                                                    .setBody(data.get("content"))
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build();

                            try {
                                fi.send(message);
                            } catch (FirebaseMessagingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );

//        List<String> tokens = fcmTokenRepository.findAll().stream()
//                .map(FcmTokenEntity::getToken)
//                .toList();
//
//        ApiFuture<BatchResponse> batchResponseApiFuture = getFirebaseInstance().sendMulticastAsync(
//                MulticastMessage.builder()
//                        .addAllTokens(tokens)
//                        .putAllData(data)
//                        .setAndroidConfig(
//                                AndroidConfig.builder()
//                                        .setNotification(
//                                                AndroidNotification.builder()
//                                                        .setTitle(data.get("title"))
//                                                        .setBody(data.get("content"))
//                                                        .build()
//                                        )
//                                        .build()
//                        )
//                        .build()
//        );
//
//        try {
//            BatchResponse response = batchResponseApiFuture.get();
//            log.info(response.getSuccessCount() + " messages were sent successfully");
//        } catch (InterruptedException | ExecutionException e) {
//            log.info("Error sending message: " + e.getMessage());
//        }
    }

    private FirebaseMessaging getFirebaseInstance() {
        return FirebaseMessaging.getInstance();
    }
}
