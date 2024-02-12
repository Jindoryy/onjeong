package com.a503.onjeong.domain.user.dto

data class FcmTokenRequestDto(
    val userId: Long,
    val fcmToken: String
)
