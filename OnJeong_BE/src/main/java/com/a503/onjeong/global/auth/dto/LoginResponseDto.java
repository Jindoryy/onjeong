package com.a503.onjeong.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


/* 로그인시 response 되는 dto */
@Data
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
}
