package com.a503.onjeong.domain.news.api

import com.a503.onjeong.domain.news.dto.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NewsApiService {

    @Headers("Content-Type: application/json")
    @GET("/news/lists")
    fun newsList(): Call<List<News>>

}