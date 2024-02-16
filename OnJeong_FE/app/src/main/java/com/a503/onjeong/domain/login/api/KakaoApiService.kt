package com.a503.onjeong.domain.login.api

import com.a503.onjeong.domain.login.dto.KakaoDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoApiService {
    @GET("/oauth/authorize")
    fun getAuthorizationCode(
        @Query("client_id") id: String,
        @Query("redirect_uri") redirect: String,
        @Query("response_type") type: String = "code",
        ): Call<Void>

    @GET("/auth/kakao/token")
    fun requestToken(
        @Query("code") code: String
    ): Call<KakaoDto.Token>
}