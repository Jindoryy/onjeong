package com.a503.onjeong.domain.login.dto

data class LoginInfoResponseDto(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val type: String
)
