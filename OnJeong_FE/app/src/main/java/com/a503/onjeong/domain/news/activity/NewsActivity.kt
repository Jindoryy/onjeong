package com.a503.onjeong.domain.news.activity


import com.a503.onjeong.domain.news.adapter.NewsAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*

import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.news.dto.NewsDto
import com.a503.onjeong.R
import com.a503.onjeong.domain.news.crawling.NewsCrawling
import com.a503.onjeong.global.network.RetrofitClient
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class NewsActivity : AppCompatActivity() {
    private lateinit var newsListView: ListView
    private lateinit var adapter: NewsAdapter
    private lateinit var selectedButton: Button
    private lateinit var mainTextView: TextView
    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private var newsList: MutableList<NewsDto> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        newsListView = findViewById(R.id.newsListView)
        mainTextView = findViewById(R.id.mainText)
        mainTextView.text = "뉴스"

        // 뉴스의 각각 카테고리 버튼을 선언
        val button1: Button = findViewById(R.id.category1)
        val button2: Button = findViewById(R.id.category2)
        val button3: Button = findViewById(R.id.category3)
        val button4: Button = findViewById(R.id.category4)
        val button5: Button = findViewById(R.id.category5)

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
        // 로딩 화면을 표시하기 위한 FrameLayout
        val loadingLayout: FrameLayout = findViewById(R.id.loadingLayout)
        // 로딩 화면을 표시
        loadingLayout.visibility = View.VISIBLE
        // 크롤링 작업을 수행하고 결과를 얻어오기 위해 NewsCrawler 객체 생성
        val crawler = NewsCrawling()
        // 코루틴을 사용하여 크롤링 작업 실행
        CoroutineScope(Dispatchers.Main).launch {
            newsList = crawler.crawling()
            adapter = NewsAdapter(this@NewsActivity, R.layout.activity_news_list, newsList)
            newsListView.adapter = adapter
            // 크롤링 완료 후 로딩 화면 숨기기
            loadingLayout.visibility = View.GONE
            button1.performClick()
        }
        adapter = NewsAdapter(this@NewsActivity, R.layout.activity_news_list, newsList)
        newsListView.adapter = adapter
        // 맨처음에는 정치에 해당하는 버튼이 눌리도록
        // 초기에 category1Button을 선택 상태로 설정
        selectedButton = button1
        selectedButton.isSelected = true

        // 각 카테고리 버튼에 대한 클릭 이벤트 처리
        val categoryClickListener = View.OnClickListener { view ->
            val selectedCategoryButton = view as Button
            // 다른 버튼이 눌리게 되면
            // 현재 선택된 카테고리 버튼의 isSelected 상태를 false로 변경
            selectedButton.isSelected = false
            // 클릭된 버튼의 isSelected 상태를 true로 변경
            selectedCategoryButton.isSelected = true
            // 현재 선택된 버튼을 갱신
            selectedButton = selectedCategoryButton

            // int 타입의 tmp 선언
            var tmp: Int = 0
// selectedCategoryButton.text에 따라 tmp에 값을 할당
            when (selectedCategoryButton.text) {
                "정치" -> tmp = 0
                "경제" -> tmp = 1
                "사회" -> tmp = 2
                "생활" -> tmp = 3
                else -> {
                    // 세계의 정보는 104로 else
                    tmp = 4
                }
            }
            // 선택된 카테고리에 해당하는 뉴스만 화면에 표시
            filterNewsByCategory(tmp)
            // 버튼을 눌러 화면 전환할 때 화면 맨위로 가게 설정
            newsListView.smoothScrollToPosition(0)
        }

        // 카테고리 버튼들에 클릭 리스너 설정
        button1.setOnClickListener(categoryClickListener)
        button2.setOnClickListener(categoryClickListener)
        button3.setOnClickListener(categoryClickListener)
        button4.setOnClickListener(categoryClickListener)
        button5.setOnClickListener(categoryClickListener)

        // 하나의 리스트를 클릭하면 해당 url로 연결되게 설정해보자
        newsListView.setOnItemClickListener { parent, view, position, id ->
            val selectedNewsItem = adapter.getItem(position)
            if (selectedNewsItem != null) {
                // 클릭된 아이템의 url을 일단 가져옴
                val url = selectedNewsItem.url
                // 가져온 url을 통해 클릭하면 기사로 연결되게 설정
                openUrl(url.toString())
            }
        }
    }


    // 선택된 카테고리에 해당하는 뉴스만 표시되도록 하는 메서드
    private fun filterNewsByCategory(category: Int) {
        adapter.filterNewsByCategory(category)
    }

    // 기사 제목이랑 설명을 누르면 url을 연결해주는 메서드
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}