package com.a503.onjeong.domain.user.service;

import org.springframework.stereotype.Service;

import java.net.UnknownHostException;

@Service
public interface AuthService {

    public void save();

    public void login(String accessToken) throws UnknownHostException;

    public void kakaoLoginOrSignUp(String code) throws UnknownHostException;

    public void savePhoneNumber(Long userId, String phoneNumber);

}
