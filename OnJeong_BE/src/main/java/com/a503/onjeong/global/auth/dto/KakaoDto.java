package com.a503.onjeong.global.auth.dto;

import lombok.Builder;
import lombok.Data;


/* 카카오 관련 Dto */
@Data
public class KakaoDto {

    @Data
    @Builder
    public static class Token {
        private String access_token;
        private String refresh_token;
    }

    /*토큰 재발급 DTO*/
    @Data
    public static class ReissueToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private Long refresh_token_expires_in;
        private Long expires_in;
    }

    @Data
    @Builder
    public static class UserInfo {
        private Long kakaoId;
        private String kakaoAccessToken;
        private String kakaoRefreshToken;
        private String nickname;
        private String profileImageUrl;
    }
}
