package com.a503.onjeong.domain.login.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.domain.login.api.KakaoApiService
import com.a503.onjeong.R
import com.a503.onjeong.global.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    // 슴겨야 하는데..
    private val kakaoClientId = "475bd7a6a8dc9f061055c6f827ab4b25"
    private val redirectUri = "http://i10a503.p.ssafy.io:8080/auth/kakao/redirect"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val retrofit = RetrofitClient.getKakaoApiClient()
        val service = retrofit.create(KakaoApiService::class.java)

        val kakaoLoginButton = findViewById<Button>(R.id.loginButton)

        kakaoLoginButton.setOnClickListener {
            val call = service.getAuthorizationCode(kakaoClientId, redirectUri)

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        val authorizationUrl = response.raw().request().url().toString()
                        val intent = Intent(this@LoginActivity, KakaoLoginActivity::class.java)
                        intent.putExtra("authorizationUrl", authorizationUrl)
                        startActivity(intent)
                    } else {
                        Log.e("KakaoAuth", "인가 코드 오류")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("KakaoAuth", "Request failed: ${t.message}")
                }
            })
        }

    }
}