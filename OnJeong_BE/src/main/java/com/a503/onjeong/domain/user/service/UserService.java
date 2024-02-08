package com.a503.onjeong.domain.user.service;

import com.a503.onjeong.domain.user.dto.FcmTokenRequestDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void updateFcmToken(FcmTokenRequestDto fcmTokenRequestDto);
}
