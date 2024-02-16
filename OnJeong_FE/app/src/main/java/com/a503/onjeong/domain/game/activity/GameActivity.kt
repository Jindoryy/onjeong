package com.a503.onjeong.domain.game.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.R

class GameActivity : AppCompatActivity() {
    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private lateinit var rank: Button
    private lateinit var game1Start: Button
    private lateinit var game2Start: Button
    private lateinit var game1Description: Button
    private lateinit var game2Description: Button
    private lateinit var mainTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)


        mainTextView = findViewById(R.id.mainText)
        mainTextView.text = "게임"
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
        // 게임 1 버튼
        game1Start = findViewById(R.id.game1_start)
        game1Start.setOnClickListener {
            val intent = Intent(this, Game1Activity::class.java)
            startActivity(intent)
        }
        // 게임 1 버튼
        game2Start = findViewById(R.id.game2_start)
        game2Start.setOnClickListener {
            val intent = Intent(this, Game2Activity::class.java)
            startActivity(intent)
        }
        game1Description = findViewById(R.id.game1_description)
        game1Description.setOnClickListener {
            val intent = Intent(this, Game1Description::class.java)
            startActivity(intent)
        }
        game2Description = findViewById(R.id.game2_description)
        game2Description.setOnClickListener {
            val intent = Intent(this, Game2Description::class.java)
            startActivity(intent)
        }
        // 랭킹 버튼
        rank = findViewById(R.id.game_rank)
        rank.setOnClickListener {
            val intent = Intent(this, GameRankActivity::class.java)
            startActivity(intent)
        }

    }
}