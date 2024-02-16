package com.a503.onjeong.domain.weather.api

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WeatherApiService {

    @POST("/weather/info")
    fun getWeatherInfo(@Body requestBody: WeatherRequestDto): Call<List<WeatherResponseDto>>
}