package com.a503.onjeong.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/* 재발급 필터 response dto */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReissueFilterResponseDto {
    private Long userId;
    private String refreshToken;
    private String accessToken;
}
