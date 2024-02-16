package com.a503.onjeong.domain.mypage.api

import com.a503.onjeong.domain.mypage.dto.PhonebookAllDTO
import com.a503.onjeong.domain.mypage.dto.PhonebookDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PhonebookApiService {

    @POST("/phonebook")
    fun phonebookSave(
        @Body phonebookAllDTO: PhonebookAllDTO
    ): Call<Void?>?

    @GET("/phonebook")
    fun phonebookList(
        @Query("userId") userId: Long, @Query("groupId") groupId: Long?
    ): Call<List<PhonebookDTO>>
}