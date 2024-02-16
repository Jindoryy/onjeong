package com.a503.onjeong.domain.user.controller;

import com.a503.onjeong.domain.user.dto.FcmTokenRequestDto;
import com.a503.onjeong.domain.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public interface UserController {
    @PatchMapping("/fcm-token")
    ResponseEntity<Void> updateFcmToken(@RequestBody FcmTokenRequestDto fcmTokenRequestDto);

    @DeleteMapping("")
    ResponseEntity<Void> deleteProfileImg(@RequestParam(name = "userId") Long userId);

    @PutMapping("")
    ResponseEntity<Void> updateProfileImg(@RequestParam(name = "userId") Long userId, MultipartFile file) throws IOException;

    @GetMapping("info")
    ResponseEntity<UserDTO> getUserInfo(@RequestParam(name = "userId")Long userId);

    @PutMapping("")
    ResponseEntity<Void> updatePhoneNum(@RequestParam(name = "userId")Long userId,@RequestParam(name = "phoneNum")String phoneNum);
}
