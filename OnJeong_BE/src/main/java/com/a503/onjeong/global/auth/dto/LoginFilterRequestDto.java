package com.a503.onjeong.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/* 로그인 필터 request dto */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginFilterRequestDto {
    private Long userId;
    private String kakaoAccessToken;
    private String kakaoRefreshToken;
}
