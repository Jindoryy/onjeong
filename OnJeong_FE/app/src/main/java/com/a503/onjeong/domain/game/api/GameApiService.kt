package com.a503.onjeong.domain.game.api

import com.a503.onjeong.domain.game.dto.UserGameDto
import com.a503.onjeong.domain.game.dto.UserGameResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface GameApiService {

    @Headers("Content-Type: application/json")
    @GET("/userGame/lists")
    // 상위 점수 10명만 가져옴
    fun topScoreList(@Query("gameId") gameId: Long): Call<List<UserGameResponseDto>>

    @Headers("Content-Type: application/json")
    @POST("/userGame")
    // 점수를 보내서 유저의 최고점수 , 이름까지 가져옴
    fun saveScore(
        @Body userGameDto: UserGameDto
    ): Call<UserGameResponseDto>

    @Headers("Content-Type: application/json")
    @GET("/userGame/details")
    // 유저 id와 game id를 보내서 유저에 대한 정보를 가져옴
    fun myScore(
        @Query("userId") userId : Long ,
        @Query("gameId") gameId : Long
    ): Call<UserGameResponseDto>

}