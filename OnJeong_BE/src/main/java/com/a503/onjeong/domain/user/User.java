package com.a503.onjeong.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "kakao_id")
    private Long kakaoId;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType type;

    @Column(name = "kakao_refresh_token")
    private String kakaoRefreshToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "profile_url")
    private String profileUrl;

    @Builder
    public User(
            Long kakaoId,
            String name,
            UserType type
    ) {
        this.kakaoId = kakaoId;
        this.name = name;
        this.type = type;
    }

    // FCM 토큰 세팅
    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // 리프레시 토큰 세팅
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 카카오 리프레시 토큰 세팅
    public void setKakaoRefreshToken(String kakaoRefreshToken) {
        this.kakaoRefreshToken = kakaoRefreshToken;
    }

    // 핸드폰 번호 세팅
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
