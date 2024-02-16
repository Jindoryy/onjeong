package com.a503.onjeong.domain.user.controller;

import com.a503.onjeong.domain.user.dto.FcmTokenRequestDto;
import com.a503.onjeong.domain.user.dto.UserDTO;
import com.a503.onjeong.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    @PatchMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(@RequestBody FcmTokenRequestDto fcmTokenRequestDto) {
        userService.updateFcmToken(fcmTokenRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Override
    @DeleteMapping("")
    public ResponseEntity<Void> deleteProfileImg(@RequestParam("userId") Long userId) {
        //db 기본이미지로 바꿔서 저장 , s3에서 파일 삭제
        userService.deleteProfileImg(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @PostMapping("")
    public ResponseEntity<Void> updateProfileImg(
            @RequestParam("userId") Long userId,@RequestParam("profile") MultipartFile file
    ) throws IOException {
        userService.updateProfileImg(userId,file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @GetMapping("info")
    public ResponseEntity<UserDTO> getUserInfo(@RequestParam("userId") Long userId) {
        UserDTO userDTO=userService.getUserInfo(userId);
        return new ResponseEntity<>(userDTO,HttpStatus.OK);
    }

    @Override
    @PutMapping("")
    public ResponseEntity<Void> updatePhoneNum(Long userId, String phoneNum) {
        userService.updatePhoneNum(userId,phoneNum);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
