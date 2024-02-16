package com.a503.onjeong.domain.user.dto;

import lombok.Data;

@Data
public class FcmTokenRequestDto {
    Long userId;
    String fcmToken;
}
