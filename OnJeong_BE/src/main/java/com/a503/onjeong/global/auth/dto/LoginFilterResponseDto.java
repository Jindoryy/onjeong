package com.a503.onjeong.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/* 로그인 필터 reponse dto */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginFilterResponseDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;
    private String kakaoAccessToken;
    private String kakaoRefreshToken;
}

