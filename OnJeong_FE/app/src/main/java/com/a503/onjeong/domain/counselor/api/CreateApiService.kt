package com.a503.onjeong.domain.counselor.api

import com.a503.onjeong.domain.counselor.dto.SessionIdRequestDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CreateApiService {
    @POST("/counselor/sessions")
    fun createSessionId(
        @Body sessionIdRequestDto: SessionIdRequestDto
    ): Call<ResponseBody>

    @POST("/counselor/sessions/{sessionId}/connections")
    fun createConnection(
        @Path("sessionId") sessionId: String
    ): Call<ResponseBody>

    @DELETE("/counselor/delete/{sessionId}")
    fun deleteRoom(
        @Path("sessionId") sessionId: String
    ): Call<ResponseBody>

    @POST("/counselor/connect")
    fun connectToEmptyRoom(
    ): Call<ResponseBody>

    @POST("/counselor/user/{sessionId}/connections")
    fun getUserToken(
        @Path("sessionId") sessionId: String
    ): Call<ResponseBody>

    @GET("/counselor/get/{sessionId}")
    fun getRoomUserId(
        @Path("sessionId") sessionId: String
    ): Call<ResponseBody>

    @POST("/counselor/disconnect/{sessionId}")
    fun userDisconnect(
        @Path("sessionId") sessionId: String
    ): Call<ResponseBody>
}