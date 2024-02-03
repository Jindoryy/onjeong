package com.a503.onjeong.domain.weather.dto



data class WeatherResponseDto (

    val date: String? = null,

    // 현재 기온
    val temperatures: Int,

    // 최저 기온
    val temperaturesLow: Int,

    // 최고 기온
    val temperaturesHigh: Int,

    // 하늘 상태
    // 맑음(1), 구름많음(3), 흐림(4)
    val sky: Int = 0,

    // 강수 형태
    // 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
    val pty: Int = 0
)