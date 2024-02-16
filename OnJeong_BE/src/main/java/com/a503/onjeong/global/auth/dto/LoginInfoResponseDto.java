package com.a503.onjeong.global.auth.dto;

import com.a503.onjeong.domain.user.UserType;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginInfoResponseDto {
    Long id;
    String name;
    String phoneNumber;
    UserType type;

    @Builder
    public LoginInfoResponseDto(
            Long id,
            String name,
            String phoneNumber,
            UserType type
    ) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.type = type;
    }
}
