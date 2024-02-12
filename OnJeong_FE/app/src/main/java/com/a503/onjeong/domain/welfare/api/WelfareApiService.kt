package com.a503.onjeong.domain.welfare.api

import com.a503.onjeong.domain.welfare.dto.WelfareResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WelfareApiService {

    @GET("gov24/v3/serviceList")
    fun welfareList(
        @Query("serviceKey") value: String,
        @Query("cond[소관기관코드::EQ]") cond: String
    ): Call<WelfareResponseDto>
}