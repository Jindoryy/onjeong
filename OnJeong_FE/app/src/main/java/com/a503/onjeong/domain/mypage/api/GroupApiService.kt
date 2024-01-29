package com.a503.onjeong.domain.mypage.api

import com.a503.onjeong.domain.mypage.dto.GroupDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface GroupApiService {

    @Headers("Content-Type: application/json")
    @GET("/group/list")
    fun groupList(
        @Query("userId") userId: Int
    ): Call<List<GroupDTO>>
}