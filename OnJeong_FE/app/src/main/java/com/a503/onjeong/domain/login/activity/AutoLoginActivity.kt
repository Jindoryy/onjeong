package com.a503.onjeong.domain.login.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.login.api.LoginApiService
import com.a503.onjeong.domain.login.dto.LoginInfoResponseDto
import com.a503.onjeong.global.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_login)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


//        sharedPreference 값 삭제 (db에는 없는데 클라에 존재해버리면 안됨, 필요시 사용)
//        editor.remove("kakaoAccessToken")
//        editor.remove("kakaoRefreshToken")
//        editor.remove("jwtAccessToken")
//        editor.remove("jwtRefreshToken")
//        editor.remove("userId")
//        editor.apply()

        val kakaoAccessToken = sharedPreferences.getString("kakaoAccessToken", "none").toString()
        val userId = sharedPreferences.getLong("userId", 0L)

        val retrofit = RetrofitClient.getAuthApiClient()
        val loginApiService = retrofit.create(LoginApiService::class.java)

        // 처음 회원 가입
        if (kakaoAccessToken != "none") {
            val call = loginApiService.login(
                kakaoAccessToken, userId
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
                        // 인증 성공, StartActivity로 이동
                        val intent = Intent(this@AutoLoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<LoginInfoResponseDto>, t: Throwable) {
                    // 로그인 페이지로
                    val intent = Intent(this@AutoLoginActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            })
        } else {
            // 로그인 페이지로
            val intent = Intent(this@AutoLoginActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
