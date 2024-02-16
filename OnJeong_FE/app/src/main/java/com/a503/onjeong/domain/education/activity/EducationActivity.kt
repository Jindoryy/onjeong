package com.a503.onjeong.domain.education.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity


class EducationActivity : AppCompatActivity() {

    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private lateinit var mainTextView: TextView
    private lateinit var category1: Button
    private lateinit var category2: Button
    private lateinit var category3: Button
    private lateinit var selectedButton: Button

    private lateinit var video1: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    private lateinit var video2: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    private lateinit var video3: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    private lateinit var video4: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    private lateinit var video5: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
    private lateinit var video6: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_education)

        // 상단바 이름 변경
        mainTextView = findViewById(R.id.mainText)
        mainTextView.text = "온라인 교육"


        category1 = findViewById(R.id.category1)
        category2 = findViewById(R.id.category2)
        category3 = findViewById(R.id.category3)

        video1 = findViewById(R.id.youtube_player_view1)
        video2 = findViewById(R.id.youtube_player_view2)
        video3 = findViewById(R.id.youtube_player_view3)
        video4 = findViewById(R.id.youtube_player_view4)
        video5 = findViewById(R.id.youtube_player_view5)
        video6 = findViewById(R.id.youtube_player_view6)
        // 영상 6개를 초기화하고
        // 카테고리누르면 2개씩 보이게 설정

        // 로딩 화면을 표시하기 위한 FrameLayout
        val loadingLayout: FrameLayout = findViewById(R.id.loadingLayout)
        // 로딩 화면을 표시
        loadingLayout.visibility = View.VISIBLE
        loadingLayout.postDelayed({
            // 로딩 화면을 숨깁니다.
            loadingLayout.visibility = View.GONE
        }, 3000)

        // 맨처음에는 카테고리1 활성화
        selectedButton = category1
        selectedButton.isSelected = true



        category1.setOnClickListener {
            video1.visibility = View.VISIBLE
            video2.visibility = View.VISIBLE
            video3.visibility = View.GONE
            video4.visibility = View.GONE
            video5.visibility = View.GONE
            video6.visibility = View.GONE
            selectedButton.isSelected = false
            selectedButton = category1
            selectedButton.isSelected = true
        }

        category2.setOnClickListener {
            video1.visibility = View.GONE
            video2.visibility = View.GONE
            video3.visibility = View.VISIBLE
            video4.visibility = View.VISIBLE
            video5.visibility = View.GONE
            video6.visibility = View.GONE
            selectedButton.isSelected = false
            selectedButton = category2
            selectedButton.isSelected = true
        }

        category3.setOnClickListener {
            video1.visibility = View.GONE
            video2.visibility = View.GONE
            video3.visibility = View.GONE
            video4.visibility = View.GONE
            video5.visibility = View.VISIBLE
            video6.visibility = View.VISIBLE
            selectedButton.isSelected = false
            selectedButton = category3
            selectedButton.isSelected = true
        }



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
}