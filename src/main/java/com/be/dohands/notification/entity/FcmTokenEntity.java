package com.be.dohands.notification.entity;


import com.be.dohands.base.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FcmTokenEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fcmTokenId;

    private String token;

    private Long userId;

    private boolean isActive;

    public FcmTokenEntity(String token, Long userId) {
        this.token = token;
        this.userId = userId;
        this.isActive = true;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
