package com.a503.onjeong.domain.login.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.login.api.LoginApiService
import com.a503.onjeong.domain.login.dto.LoginInfoResponseDto
import com.a503.onjeong.global.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartActivity : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_page)

        val startButton = findViewById<Button>(R.id.buttonStart)
        val retrofit = RetrofitClient.getAuthApiClient()
        val loginApiService = retrofit.create(LoginApiService::class.java)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        startButton.setOnClickListener {
            val call = loginApiService.login(
                sharedPreferences.getString("kakaoAccessToken", "none").toString(),
                sharedPreferences.getLong("userId", 0L)
            )

            call.enqueue(object : Callback<LoginInfoResponseDto> {
                override fun onResponse(
                    call: Call<LoginInfoResponseDto>,
                    response: Response<LoginInfoResponseDto>
                ) {
                    if (response.isSuccessful) {

                        val loginInfoResponseDto = response.body()

                        val userId: Long? = loginInfoResponseDto?.id
                        val name: String? = loginInfoResponseDto?.name
                        val type: String? = loginInfoResponseDto?.type
                        val headers = response.headers()
                        val jwtAccessToken = headers.get("Authorization")
                        val jwtRefreshToken = headers.get("Refresh-Token")
                        val kakaoAccessToken = headers.get("Kakao-Access-Token")

                        if (userId != null) {
                            editor.putLong("userId", userId)
                            editor.putString("name", name)
                            editor.putString("type", type)
                        }
                        editor.putString("jwtAccessToken", jwtAccessToken)
                        editor.putString("jwtRefreshToken", jwtRefreshToken)
                        editor.putString("kakaoAccessToken", kakaoAccessToken)
                        editor.apply()

                        // 인증 성공, MainActivity로 이동
                        val intent = Intent(this@StartActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.e("Login", "로그인 과정 중 오류가 생겼습니다.")
                    }
                }


                override fun onFailure(call: Call<LoginInfoResponseDto>, t: Throwable) {
                    Log.e("Login", "Request failed: ${t.message}")
                }
            })
        }
    }
}
