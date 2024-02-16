package com.a503.onjeong.domain.user.service;

import com.a503.onjeong.domain.user.User;
import com.a503.onjeong.domain.user.dto.FcmTokenRequestDto;
import com.a503.onjeong.domain.user.dto.UserDTO;
import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.global.util.S3Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String IMG_PATH="https://allfriend.s3.ap-northeast-2.amazonaws.com";
    private static final String DefaultProfileImg="https://allfriend.s3.ap-northeast-2.amazonaws.com/profile_img.png";
    private final UserRepository userRepository;
    private final S3Util s3Util;

    @Override
    @Transactional
    public void updateFcmToken(FcmTokenRequestDto fcmTokenRequestDto) {
        Long userId = fcmTokenRequestDto.getUserId();
        String fcmToken = fcmTokenRequestDto.getFcmToken();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;
        if (fcmToken.equals(user.getFcmToken())) return;

        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteProfileImg(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        if (!user.getProfileUrl().equals(DefaultProfileImg))
            s3Util.deleteFile(user.getProfileUrl());
        user.setProfileUrl(DefaultProfileImg);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateProfileImg(Long userId, MultipartFile file) throws IOException {
        //user의 url 바꾸기
        User user = userRepository.findById(userId).orElseThrow();
        if (!user.getProfileUrl().equals(DefaultProfileImg))
            s3Util.deleteFile(user.getProfileUrl());
        String path = s3Util.uploadFile(file);
        user.setProfileUrl(IMG_PATH+s3Util.getFile(path));
        userRepository.save(user);
    }

    @Override
    public UserDTO getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        UserDTO userDTO=UserDTO.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .build();
        userDTO.setProfileUrl(user.getProfileUrl());
        return userDTO;
    }

    @Override
    @Transactional
    public void updatePhoneNum(Long userId, String phoneNum) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setPhoneNumber(phoneNum);
        userRepository.save(user);
    }
}
