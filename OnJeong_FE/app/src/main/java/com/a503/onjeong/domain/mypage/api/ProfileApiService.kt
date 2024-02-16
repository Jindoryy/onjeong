package com.a503.onjeong.domain.mypage.api

import com.a503.onjeong.domain.mypage.dto.UserDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface ProfileApiService {

    @Multipart
    @POST("user")
    fun profileUpdate(
        @Part("userId") userId: Long,
        @Part() imageFile: MultipartBody.Part
    ): Call<Void>

    @DELETE("user")
    fun profileDelete(
        @Query("userId") userId: Long
    ):Call<Void>

    @GET("user/info")
    fun getUserInfo(
        @Query("userId") userId: Long
    ):Call<UserDTO>

    @PUT("user")
    fun updatePhoneNum(
        @Query("userId") userId: Long,
        @Query("phoneNum") phoneNum:String
    ):Call<Void>

}