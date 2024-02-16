package com.a503.onjeong.global.network.interceptor

import android.content.Context
import android.content.Intent
import com.a503.onjeong.domain.login.activity.LoginActivity
import com.a503.onjeong.domain.login.api.LoginApiService
import com.a503.onjeong.global.network.RetrofitClient
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code() == 401) {
            // refreshToken을 사용하여 토큰 갱신
            val retrofit = RetrofitClient.getApiClient(this.context)
            val service = retrofit.create(LoginApiService::class.java)

            val sharedPreferences =
                context.getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val call =
                service.reissue(sharedPreferences.getString("jwtRefreshToken", null).toString())

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        val accessToken = response.headers().get("Authorization").toString()
                        val refreshToken = response.headers().get("Refresh-Token").toString()
                        // SharedPreferences에 토큰 저장
                        editor.putString("jwtAccessToken", accessToken)
                        editor.putString("jwtRefreshToken", refreshToken)
                        editor.apply()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // 실패하면 로그인 페이지로
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    return
                }
            })

            val accessToken = sharedPreferences.getString("jwtAccessToken", null).toString()

            if (accessToken.isNotEmpty()) {
                // 토큰을 갱신한 후 원래의 요청에 새로운 액세스 토큰을 추가
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", accessToken)
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return response
    }

}