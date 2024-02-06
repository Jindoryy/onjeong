package com.a503.onjeong.domain.news.api

import com.a503.onjeong.domain.news.dto.NewsDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface NewsApiService {

    @Headers("Content-Type: application/json")
    @GET("/news/lists")
    fun newsList(): Call<List<NewsDto>>

}