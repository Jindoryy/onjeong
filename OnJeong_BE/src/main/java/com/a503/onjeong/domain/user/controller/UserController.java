package com.a503.onjeong.domain.user.controller;

import com.a503.onjeong.domain.user.dto.FcmTokenDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public interface UserController {
    @PatchMapping("/fcm-token")
    ResponseEntity<Void> updateFcmToken(@RequestBody FcmTokenDto fcmTokenDto);
}
