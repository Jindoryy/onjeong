package com.a503.onjeong.global.network.interceptor


import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // access token 추가 해서 반환
        val sharedPreferences = context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val accessToken = sharedPreferences.getString("jwtAccessToken", null).toString()
        val modifiedRequest = request.newBuilder()
            .addHeader("Authorization", accessToken)
            .build()

        return chain.proceed(modifiedRequest)
    }
}