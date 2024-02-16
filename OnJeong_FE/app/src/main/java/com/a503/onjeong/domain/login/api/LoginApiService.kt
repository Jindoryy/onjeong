package com.a503.onjeong.domain.login.api

import com.a503.onjeong.domain.login.dto.LoginInfoResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LoginApiService {

    @GET("/auth/signup")
    fun signup(
        @Header("Kakao-Access-Token") accessToken: String,
        @Header("Kakao-Refresh-Token") refreshToken: String,
        @Query("phoneNumber") phoneNumber : String
    ): Call<Long>

    @GET("/auth/login")
    fun login(
        @Header("Kakao-Access-Token") accessToken: String,
        @Query("userId") userId : Long
    ): Call<LoginInfoResponseDto>

    @GET("auth/phone")
    fun phone(
        @Query("phoneNumber") phoneNumber :String
    ): Call<String>

    @GET("/auth/reissue")
    fun reissue(
        @Header("Refresh-Token") refreshToken :String
    ): Call<Void>
}