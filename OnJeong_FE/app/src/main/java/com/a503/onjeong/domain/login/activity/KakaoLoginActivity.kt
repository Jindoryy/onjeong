package com.a503.onjeong.domain.login.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.login.dto.KakaoDto
import com.a503.onjeong.global.network.RetrofitClient
import com.a503.onjeong.domain.login.api.KakaoApiService

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KakaoLoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_webview)

        val authorizationUrl = intent.getStringExtra("authorizationUrl")

        val webView = findViewById<WebView>(R.id.webview)
        // WebView 설정
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()

        // WebViewClient를 통해 리디렉션된 URI 처리
        webView.webViewClient = object : WebViewClient() {
            @SuppressLint("NewApi")
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url
                if (uri != null && uri.toString().startsWith("http://i10a503.p.ssafy.io:8080/auth/kakao/redirect")) {
                    // 여기서 URI 파싱 등을 통해 인가 코드 추출
                    val authorizationCode = extractAuthorizationCode(uri)

                    // 추출한 인가 코드를 사용하여 토큰 재발급
                    requestToken(authorizationCode)

                    // true를 반환하여 WebView가 이 URL을 로드하지 않도록 함
                    return true
                }

                // 기본적인 URL 로딩 허용
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        // WebView에 인가 URL 로딩
        webView.loadUrl(authorizationUrl.toString())
    }

    private fun extractAuthorizationCode(uri: Uri): String {
        // 여기서 URI 파싱 등을 통해 인가 코드 추출 로직을 작성
        val code = uri.getQueryParameter("code")
        return code.orEmpty() // orEmpty()는 null이면 빈 문자열을 반환
    }

    private fun requestToken(authorizationCode: String) {
        val retrofit = RetrofitClient.getAuthApiClient()

        val service = retrofit.create(KakaoApiService::class.java)
        val call = service.requestToken(authorizationCode)

        call.enqueue(object : Callback<KakaoDto.Token> {
            override fun onResponse(call: Call<KakaoDto.Token>, response: Response<KakaoDto.Token>) {
                if (response.isSuccessful) {

                    val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    val accessToken = response.body()?.accessToken
                    val refreshToken = response.body()?.refreshToken
                    // SharedPreferences에 토큰 저장
                    editor.putString("kakaoAccessToken", accessToken)
                    editor.putString("kakaoRefreshToken", refreshToken)
                    editor.apply()

                    // 다음 액티비티로 이동
                    val intent = Intent(this@KakaoLoginActivity, TelephoneActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("KakaoAuth", "인가 코드 오류")
                }
            }

            override fun onFailure(call: Call<KakaoDto.Token>, t: Throwable) {
                Log.e("KakaoAuth", "Request failed: ${t.message}")
            }
        })
    }
}