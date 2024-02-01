package com.a503.onjeong.domain.game.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface GameApiService {
    @Headers("Content-Type: application/json")
    @GET("/game/lists")
    fun topScoreList(): Call<List<Game>>

}