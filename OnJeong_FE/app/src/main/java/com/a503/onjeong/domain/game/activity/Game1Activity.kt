package com.a503.onjeong.domain.game.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.RectF
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.game.api.GameApiService
import com.a503.onjeong.domain.game.dto.UserGameDto
import com.a503.onjeong.domain.game.dto.UserGameResponseDto
import com.a503.onjeong.global.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Game1Activity : AppCompatActivity() {
    private lateinit var start: Button
    private lateinit var exit: Button
    private lateinit var rank: Button
    private lateinit var timeTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var gameMarkTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var imageViews: List<ImageView>
    private var selectedIndex = -1

    private lateinit var frameLayout: FrameLayout
    private lateinit var gridLayout: GridLayout
    private lateinit var mainLayout: LinearLayout
    private lateinit var pauseButton: Button
    private lateinit var resumeButton: Button
    private lateinit var exitButton: Button
    private lateinit var mainBar: LinearLayout
    private lateinit var scoreBar: LinearLayout
    private lateinit var lenearFrame: LinearLayout

    private val gameImages = listOf(
        R.drawable.game_image7,
        R.drawable.game_image8,
        R.drawable.game_image9,
        R.drawable.game_image10,
        R.drawable.game_image11,
        R.drawable.game_image12,
        R.drawable.game_image13,
    )
    private var imageNum = (0..48).toMutableList()
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Long = 0
    private var score: Long = 0
    private var isTimerRunning = false
    private var remainTime: Int = 0

    // 각 ImageView의 좌표 범위를 저장할 리스트
    private val imageViewCoordinates = mutableListOf<RectF>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game1_ready)
        // 유저이름 가져옴
        sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        userId = sharedPreferences.getLong("userId", 0L)
        // 종료버튼
        exit = findViewById(R.id.end)
        exit.setOnClickListener {
            var intent = Intent(this, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        // 시작버튼
        start = findViewById(R.id.start)
        start.setOnClickListener {
            setContentView(R.layout.activity_game1_start)
            initializeViews(49)
            // 정지 , 재게 , 종료버튼 선언 및 초기화
            pauseButton = findViewById(R.id.pauseButton)
            frameLayout = findViewById(R.id.frameLayout)
            gridLayout = findViewById(R.id.gridLayout)
            mainLayout = findViewById(R.id.game1_mainbar)

            resumeButton = findViewById(R.id.gameResume)
            exitButton = findViewById(R.id.gameExit)
            mainBar = findViewById(R.id.game1_mainbar)
            scoreBar = findViewById(R.id.game1_scoreBar)
            lenearFrame = findViewById(R.id.frameLinear)
            pauseButton.setOnClickListener {
                pauseTimer()
                gridLayout.visibility = View.GONE
                mainBar.visibility = View.GONE
                scoreBar.visibility = View.GONE
                lenearFrame.visibility = View.VISIBLE
            }

            resumeButton.setOnClickListener {
                resumeTimer()
                gridLayout.visibility = View.VISIBLE
                mainBar.visibility = View.VISIBLE
                scoreBar.visibility = View.VISIBLE
                lenearFrame.visibility = View.GONE
            }

            exitButton.setOnClickListener {
                var intent = Intent(this, GameActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    private fun startTimer(gameTime: Int) {
        countDownTimer = object : CountDownTimer(gameTime.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timeTextView.text = "$secondsLeft 초"
                remainTime = millisUntilFinished.toInt()
            }

            override fun onFinish() {
                timeTextView.text = "끝"
                endGame()
            }
        }.start()
        isTimerRunning = true
    }

    private fun initializeViews(size: Int) {
        timeTextView = findViewById(R.id.time_text)
        scoreTextView = findViewById(R.id.score_text)
        gameMarkTextView = findViewById(R.id.game_mark)
        gameMarkTextView.text = "퍼즐 게임"
        val imageIds = mutableListOf<Int>()
        for (i in 1..49) {
            val resourceId = resources.getIdentifier("game1_block$i", "id", packageName)
            imageIds.add(resourceId)
        }

        imageViews = mutableListOf()
        for (i in 0 until size) {
            val imageView = findViewById<ImageView>(imageIds[i])
            (imageViews as MutableList<ImageView>).add(imageView)
        }
        // 이미지뷰의 좌표를 RectF로 저장
        imageViews.forEach { imageView ->
            val rect = RectF()
            imageView.viewTreeObserver.addOnGlobalLayoutListener {
                val location = IntArray(2)
                imageView.getLocationOnScreen(location)
                rect.set(
                    location[0].toFloat(),
                    location[1].toFloat(),
                    (location[0] + imageView.width).toFloat(),
                    (location[1] + imageView.height).toFloat()
                )
            }
            imageViewCoordinates.add(rect)
        }

        // 이미지뷰에 터치 리스너 등록
        for (i in imageViews.indices) {
            imageViews[i].setOnTouchListener(MyTouchListener(i))
        }
        setRandomColors()
        while (blockCheck()) {
        }
        startTimer(100000)
        score = 0
        scoreTextView.text = "$score 점"
    }

    private fun setRandomColors() {
        for ((index, imageView) in imageViews.withIndex()) {
            val randomImage = gameImages.random()
            imageNum[index] = randomImage
            imageView.setImageResource(randomImage)
        }
    }

    private fun blockCheck(): Boolean {
        // 가로로 3개 이상인 블록 찾아서 색을 바꿈
        var flag1: Boolean = false
        var flag2: Boolean = false
        var changingBlocks: MutableList<Int> = mutableListOf()
        for (i in 0 until 7) {
            for (j in 0 until 5) {
                for (k in 3..7) {
                    if (j + k > 7) {
                        continue
                    }
                    val horizontalBlocks = (0 until k).map { i * 7 + j + it }
                    if (checkColor(horizontalBlocks)) {
                        flag1 = true
                        changingBlocks.addAll(horizontalBlocks)
                        continue
                    }
                }
            }
        }
        // 세로로 3개 이상인 블록 찾아서 색을 바꿈
        for (i in 0 until 5) {
            for (j in 0 until 7) {
                for (k in 3..7) {
                    if (i + k > 7) {
                        continue
                    }
                    val verticalBlocks = (0 until k).map { (i + it) * 7 + j }
                    if (checkColor(verticalBlocks)) {
                        flag2 = true
                        changingBlocks.addAll(verticalBlocks)
                        continue
                    }
                }
            }
        }
        // 만약 터지는 블럭이 있으면 true 반환
        if (flag1 || flag2) {
            changeColor(changingBlocks) // 실험 필요 ㄱ자
            return true
        }
        return false
    }

    private fun checkColor(blocks: List<Int>): Boolean {
        val firstColor = imageNum[blocks[0]]
        for (block in blocks) {
            if (imageNum[block] != firstColor) {
                return false
            }
        }
        return true
    }

    private fun changeColor(blocks: List<Int>) {
        score = score + (blocks.size * 10)
        scoreTextView.text = "$score 점"
        // 검정으로 전환 후 랜덤색으로
        for (index in blocks.indices) {
            Handler(Looper.getMainLooper()).postDelayed({
                imageViews[blocks[index]].setImageResource(R.color.heavy_gray)
            }, 50)
        }

        for (index in blocks.indices) {
            val randomImage = gameImages.random()
            imageNum[blocks[index]] = randomImage
            Handler(Looper.getMainLooper()).postDelayed({
                imageViews[blocks[index]].setImageResource(randomImage)
            }, 100)
        }
    }

    // 두개의 색의위치를 바꾸는 메서드
    private fun switch(index1: Int, index2: Int) {
        val temp = imageNum[index1]
        imageNum[index1] = imageNum[index2]
        imageNum[index2] = temp
        imageViews[index1].setImageResource(imageNum[index1])
        imageViews[index2].setImageResource(imageNum[index2])
    }

    // 각자의 색깔을 움직이기 위한 메서드
    inner class MyTouchListener(private val index: Int) : View.OnTouchListener {
        // 주변이미지 저장할 배열 매 클릭마다 초기화
        private val aroundImageView = mutableListOf<RectF>()
        private val aroundImageViewIndex = mutableListOf<Int>()
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 사용자가 화면을 처음 터치했을 때
                    selectedIndex = index
                    // 클릭된 이미지뷰의 주변 이미지뷰를 
                    // aroundImageView에 넣음

                    var tmp = index - 1
                    if (tmp >= 0 && index % 7 != 0) {
                        addAroundView(tmp)
                    }
                    tmp = index + 1
                    if (tmp <= 48 && tmp % 7 != 0) {
                        addAroundView(tmp)
                    }
                    tmp = index - 7
                    if (tmp >= 0) {
                        addAroundView(tmp)
                    }
                    tmp = index + 7
                    if (tmp <= 48) {
                        addAroundView(tmp)
                    }
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    // 사용자가 손가락을 화면에서 뗐을 때
                    // 선택된 이미지뷰와 가장 가까운 이미지뷰 찾기
                    val closestIndex = findClosestImageView(event.rawX, event.rawY)
                    if (closestIndex != -1 && selectedIndex != closestIndex) {
                        var tmp = selectedIndex
                        // 선택된 이미지뷰와 가장 가까운 이미지뷰와 위치 교환
                        switch(selectedIndex, closestIndex) // 두개 바꾼이후 터지는지 확인
                        if (!blockCheck()) {
                            // 점수가 오르지 않으면 0.1초후 다시 원위치
                            Handler(Looper.getMainLooper()).postDelayed({
                                // 왜 switch 안에 selectedIndex를 넣으면 -1로 초기화될까
                                switch(tmp, closestIndex)
                            }, 100)
                        }
                        // blockCheck 가 false나올때까지 돌음
                        else {
                            while (blockCheck()) {
                            }
                        }
                    }
                    selectedIndex = -1
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                }
            }
            return false
        }

        private fun addAroundView(index: Int) {
            aroundImageViewIndex.add(index)
            aroundImageView.add(imageViewCoordinates.get(index))
        }

        // 선택된 좌표와 가장 가까운 이미지뷰의 인덱스 찾기
        private fun findClosestImageView(x: Float, y: Float): Int {
            var closestDistance = Float.MAX_VALUE
            var closestIndex = -1

            for ((i, rect) in aroundImageView.withIndex()) {
                val distance = calculateDistance(x, y, rect.centerX(), rect.centerY())
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestIndex = i
                }
            }
            return aroundImageViewIndex.get(closestIndex)
        }

        // 두 좌표 사이의 거리 계산
        private fun calculateDistance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return Math.hypot(x1.toDouble() - x2.toDouble(), y1.toDouble() - y2.toDouble())
                .toFloat()
        }
    }


    private fun sendScore(userId: Long, gameId: Long, score: Long) {
        // NetRetrofit을 생성
        val retrofit = RetrofitClient.getApiClient(this)
        // NetRetrofit의 service를 통해 호출
        val service = retrofit.create(GameApiService::class.java)
        // UserGameDto 생성
        val userGameDto = UserGameDto(userId, gameId, score)
        var call = service.saveScore(userGameDto)

        call.enqueue(object : Callback<UserGameResponseDto> {
            override fun onResponse(
                call: Call<UserGameResponseDto>,
                response: Response<UserGameResponseDto>
            ) {
                if (response.isSuccessful) {
                    // 성공적으로 서버에 전송된 경우
                    // 추가적인 작업 수행
                    val userGameInfo = response.body()
                    if (userGameInfo != null) {
                        // 순서대로 1. 내점수  2. 플레이어 이름  3. 플레이어 최고점수
                        var tmpName: String = userGameInfo.userName
                        var tmpScore: String = userGameInfo.userGameScore.toString()
                        findViewById<TextView>(R.id.result_name).text = "유저 이름 : $tmpName "
                        findViewById<TextView>(R.id.result_high_score).text = "최고 점수 : $tmpScore 점"

                    }
                } else {
                    // 스프링에서 정보 불러오기 실패 시 호출
                    Log.d("실패", "실패 : ${response.code()} ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserGameResponseDto>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })
    }

    private fun endGame() {
        setContentView(R.layout.activity_game_result)
        findViewById<TextView>(R.id.result_score).text = "현재 점수 : $score 점"
        sendScore(userId, 1, score)

        exit = findViewById(R.id.game_exit)
        rank = findViewById(R.id.game_rank)
        exit.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        rank.setOnClickListener {
            val intent = Intent(this, GameRankActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
    // 타이머 정지
    private fun pauseTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel()
            isTimerRunning = false
        }
    }
    // 타이머 재시작
    private fun resumeTimer() {
        if (!isTimerRunning) {
            startTimer(remainTime) // 남은 시간을 받아와서 타이머 다시 시작
        }
    }

}
