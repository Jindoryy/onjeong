package com.a503.onjeong.global.network

import com.a503.onjeong.global.network.interceptor.AuthInterceptor
import android.content.Context
import com.a503.onjeong.global.network.interceptor.JwtInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient  {
    private const val BASE_URL = "http://i10a503.p.ssafy.io:8080"
    private const val KAKAO_BASE_URL = "https://kauth.kakao.com"
    private lateinit var okHttpClient: OkHttpClient

    fun getApiClient(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor(JwtInterceptor(context))
                .addInterceptor(AuthInterceptor(context))
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAuthApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getKakaoApiClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}