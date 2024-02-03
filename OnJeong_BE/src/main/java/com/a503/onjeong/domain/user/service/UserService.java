package com.a503.onjeong.domain.user.service;

import com.a503.onjeong.domain.user.dto.FcmTokenDto;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void updateFcmToken(FcmTokenDto fcmTokenDto);
}
