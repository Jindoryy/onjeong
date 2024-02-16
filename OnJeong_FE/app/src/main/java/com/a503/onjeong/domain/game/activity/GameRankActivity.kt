package com.a503.onjeong.domain.game.activity;

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.game.adapter.GameAdapter
import com.a503.onjeong.domain.game.api.GameApiService
import com.a503.onjeong.domain.game.dto.UserGameResponseDto
import com.a503.onjeong.global.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GameRankActivity : AppCompatActivity() {
    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private lateinit var rankListView: ListView
    private lateinit var adapter: GameAdapter
    private lateinit var selectedButton: Button
    private lateinit var mainTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Long = 0
    private var myRank: String = "-"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_rank)
        // 유저이름 가져옴
        sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("userId", 0L)
        // 게임 종류별로 띄움
        val button1: Button = findViewById(R.id.game1_rank)
        val button2: Button = findViewById(R.id.game2_rank)
        var tmp: Long = 1
        // 초기에 game1 Button을 선택 상태로 설정
        selectedButton = button1
        selectedButton.isSelected = true

        // 각 카테고리 버튼에 대한 클릭 이벤트 처리
        val gameCategoryClickListener = View.OnClickListener { view ->
            val selectedGameCategoryButton = view as Button
            // 다른 버튼이 눌리게 되면
            // 현재 선택된 카테고리 버튼의 isSelected 상태를 false로 변경
            selectedButton.isSelected = false
            // 클릭된 버튼의 isSelected 상태를 true로 변경
            selectedGameCategoryButton.isSelected = true
            // 현재 선택된 버튼을 갱신
            selectedButton = selectedGameCategoryButton
// selectedCategoryButton.text에 따라 tmp에 값을 할당
            when (selectedGameCategoryButton.text) {
                "퍼즐 게임" -> tmp = 1
                "짝 맞추기" -> tmp = 2
                else -> {
                    tmp = 3
                }
            }
            getScoreList(userId, tmp)
        }
        mainTextView = findViewById(R.id.mainText)
        mainTextView.text = "순위"
        homeButton = findViewById(R.id.btnHome)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        // 뒤로가기 버튼 누르면 뒤로(메인)이동
        backButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        // 초기 페이지 로딩 시 한 번 실행
        gameCategoryClickListener.onClick(button1)
        // 카테고리 버튼들에 클릭 리스너 설정
        button1.setOnClickListener(gameCategoryClickListener)
        button2.setOnClickListener(gameCategoryClickListener)


    }

    private fun getScoreList(userId: Long, tmp: Long) {
        rankListView = findViewById(R.id.rankListView)
        // NetRetrofit을 생성
        val retrofit = RetrofitClient.getApiClient(this)
        // NetRetrofit의 service를 통해 newsList 호출
        val service = retrofit.create(GameApiService::class.java)
        var callList = service.topScoreList(tmp)
        var callDetails = service.myScore(userId, tmp)
        // response가 null이 아니면 enqueue 호출
        callList.enqueue(object : Callback<List<UserGameResponseDto>> {
            override fun onResponse(
                call: Call<List<UserGameResponseDto>>,
                response: Response<List<UserGameResponseDto>>
            ) {
                // 성공 시 호출
                if (response.isSuccessful) {
                    val scoreList = response.body() ?: emptyList()
                    for ((index, userGameResponseDto) in scoreList.withIndex()) {
                        if (userGameResponseDto.userId == userId) {
                            myRank = (index + 1).toString()
                            // 리스트를 제대로 불러왔을 때 내 정보 가져옴
                            callDetails.enqueue(object : Callback<UserGameResponseDto> {
                                override fun onResponse(
                                    call: Call<UserGameResponseDto>,
                                    response: Response<UserGameResponseDto>
                                ) { // 성공 시 호출
                                    if (response.isSuccessful) {
                                        var userGameInfo = response.body()
                                        if (userGameInfo != null) {
                                            // 순서대로 1. 내이름  2. 플레이어 최고점수
                                            findViewById<TextView>(R.id.my_rank_name).text =
                                                userGameInfo.userName
                                            findViewById<TextView>(R.id.my_rank_score).text =
                                                userGameInfo.userGameScore.toString()
                                            findViewById<TextView>(R.id.my_rank_no).text = myRank

                                        }
                                    } else {
                                        // 스프링에서 정보 불러오기 실패 시 호출
                                        findViewById<TextView>(R.id.my_rank_name).text = "점수 없음"
                                        findViewById<TextView>(R.id.my_rank_score).text = " - "
                                        findViewById<TextView>(R.id.my_rank_no).text = " - "
                                    }
                                }

                                override fun onFailure(
                                    call: Call<UserGameResponseDto>,
                                    t: Throwable
                                ) {
                                    Log.e("Game", "랭크 시스템 중 오류가 생겼습니다.")
                                }
                            })

                            break
                        } else {
                            // 스프링에서 정보 불러오기 실패 시 호출
                            findViewById<TextView>(R.id.my_rank_name).text = "점수 없음"
                            findViewById<TextView>(R.id.my_rank_score).text = " - "
                            findViewById<TextView>(R.id.my_rank_no).text = " - "
                        }
                    }
                    adapter = GameAdapter(
                        this@GameRankActivity,
                        R.layout.activity_game_rank_list,
                        scoreList
                    )
                    rankListView.adapter = adapter
                    // 게임종류별로 데이터가 뜨게 설정
//                        filterRankByCategory(100)


                } else {
                    // 스프링에서 정보 불러오기 실패 시 호출
                    Log.d("실패", "실패 : ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<UserGameResponseDto>>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })


    }
}

