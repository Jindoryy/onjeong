package com.a503.onjeong.domain.user.api

import com.a503.onjeong.domain.user.dto.FcmTokenDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PATCH

interface UserApiService {
    @PATCH("/user/fcm-token")
    fun patchFcmToken(
        @Body fcmTokenDto: FcmTokenDto
    ): Call<Void>
}