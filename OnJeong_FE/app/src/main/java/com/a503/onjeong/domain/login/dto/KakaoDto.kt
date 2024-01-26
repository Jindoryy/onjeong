package com.a503.onjeong.domain.login.dto


import com.google.gson.annotations.SerializedName

data class KakaoDto(
    val token: Token
) {
    data class Token(
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("refresh_token") val refreshToken: String
    )
}