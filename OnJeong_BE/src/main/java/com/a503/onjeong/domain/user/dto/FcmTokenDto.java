package com.a503.onjeong.domain.user.dto;

import lombok.Data;

@Data
public class FcmTokenDto {
    Long userId;
    String fcmToken;
}
