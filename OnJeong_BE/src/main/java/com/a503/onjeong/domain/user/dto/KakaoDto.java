package com.a503.onjeong.domain.user.dto;

import lombok.Data;

@Data
public class KakaoDto {

    @Data
    public static class Token {
        private String access_token;
        private String refresh_token;
    }

    @Data
    public static class UserInfo {
        private Long kakaoId;
        private String nickname;
        private String profileImageUrl;
    }
}
