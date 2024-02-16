package com.a503.onjeong.domain.welfare.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.welfare.adapter.WelfareAdapter
import com.a503.onjeong.domain.welfare.api.WelfareApiService
import com.a503.onjeong.domain.welfare.dto.WelfareData
import com.a503.onjeong.domain.welfare.dto.WelfareResponseDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WelfareActivity : AppCompatActivity() {

    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private lateinit var mainTextView: TextView
    private lateinit var welfareList: RecyclerView
    private lateinit var welfareAdapter: WelfareAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welfare)

        // 상단바 이름 변경
        mainTextView = findViewById(R.id.mainText)
        mainTextView.text = "복지 서비스"

        welfareList = findViewById(R.id.recyclerView)
        welfareAdapter = WelfareAdapter(emptyList())
        welfareList.adapter = welfareAdapter
        welfareList.layoutManager = LinearLayoutManager(this)
        getWelfareList()

        // 홈버튼 누르면 홈으로 이동하게
        homeButton = findViewById(R.id.btnHome)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        // 뒤로가기 버튼 누르면 뒤로(메인)이동
        backButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    // Open API 호출
    private fun getWelfareList() {

        val serviceKey = "LSshepaOB2n2a8KxVinXlRcjWRndyzQGzA9JA4yGcgoIb54G8m/tnMfBL1nyYoWT+UCOQ0AwLNy9REeQ6rwZ4Q=="
        val cond = "3140000"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.odcloud.kr/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service = retrofit.create(WelfareApiService::class.java)

        service.welfareList(serviceKey, cond).enqueue(object : Callback<WelfareResponseDto> {
            override fun onResponse(
                call: Call<WelfareResponseDto>,
                response: Response<WelfareResponseDto>
            ) {
                if (response.isSuccessful) {
                    val welfareList = response.body()
                    if (welfareList != null) {
                        // 서버로부터 성공적으로 목록을 받았을 때 처리
                        updateRecyclerView(welfareList.data)
                    } else {
                        // 목록이 null인 경우 처리
                    }
                } else {
                    Log.d("서버로 부터 실패", "응답 코드: ${response.code()}, 메시지: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WelfareResponseDto>, t: Throwable) {
                Log.d("실패", "그냥 실패")
            }
        })
    }

    private fun updateRecyclerView(welfareList: List<WelfareData>) {
        // RecyclerView의 어댑터에 목록을 설정하여 갱신
        welfareAdapter.submitList(welfareList)
    }
}