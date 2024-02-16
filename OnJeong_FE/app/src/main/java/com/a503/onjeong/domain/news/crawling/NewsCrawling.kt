package com.a503.onjeong.domain.news.crawling

import com.a503.onjeong.domain.news.dto.NewsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException


// 뉴스 크롤링 클래스
class NewsCrawling {
    // 크롤링 메서드
    suspend fun crawling(): MutableList<NewsDto> {
        // 네이버 뉴스 메인 URL
        val NEWS_URL = "https://news.naver.com/main/main.naver?mode=LSD&mid=shm&sid1="
        // 각 카테고리별 URL 코드
        val urlCode = intArrayOf(100, 101, 102, 103, 104) // 정치 / 경제 / 사회 / 생활 및 문화 / 세계
        // 크롤링한 뉴스 정보를 담을 리스트
        val newsList: MutableList<NewsDto> = ArrayList()

        // 코루틴을 사용하여 IO 작업을 비동기적으로 처리
        withContext(Dispatchers.IO) {
            try {
                // 카테고리별로 반복하여 크롤링
                for (index in urlCode.indices) {
                    // 해당 카테고리의 페이지에 접근하여 HTML 문서 가져오기
                    val document: Document = Jsoup.connect("$NEWS_URL${urlCode[index]}").get()
                    // 제목, 내용, 이미지 주소를 추출하기 위해 각 요소 선택
                    val content1: Elements = document.select(".sa_text_title") // 제목
                    val content2: Elements = document.select(".sa_text_lede") // 내용
                    val content3: Elements = document.select(".sa_thumb_link > img") // 이미지 주소
                    // 각 카테고리의 상위 5개 기사에 대해 정보 수집
                    for (i in 0 until 8) {
                        // 기사의 제목, 내용, URL, 이미지 주소 추출
                        val title: String = content1[i].text() // 제목
                        val description: String = content2[i].text() // 내용
                        val url: String = content1[i].absUrl("href") // URL
                        val image: String = content3[i].attr("data-src") // 이미지 주소
                        // NewsDto 객체 생성 및 정보 할당
                        val newsDto = NewsDto(title, index, description, url, image)
                        // 크롤링한 기사 정보를 임시 리스트에 추가
                        newsList.add(newsDto)
                    }
                }
            } catch (e: IOException) {
                // IO 예외 처리
                e.printStackTrace()
            }
        }
        // 크롤링한 모든 기사 정보를 담은 리스트 반환
        return newsList
    }
}
