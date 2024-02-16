package com.a503.onjeong.domain.news.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.a503.onjeong.domain.news.dto.NewsDto
import com.a503.onjeong.R

class NewsAdapter(context: Context, resource: Int, objects: List<NewsDto>) :
    ArrayAdapter<NewsDto>(context, resource, objects) {
        // 카테고리에 따라 뉴스를 필터링하기 위해
        // adapter로 25개의 기사를 불러온 후에
        // object의 리스트를 newsList에 할당한다.
    private val newsDtoList: List<NewsDto> = objects.toList()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = convertView ?: inflater.inflate(R.layout.activity_news_list, parent, false)
        // 현재 기사에 해당하는 정보를 newsItem에 할당하고
        // 각각 이름 , 설명, 이미지Url에 해당하는 정보들을
        // 변수에 저장한다.
        val newsItem = getItem(position)
        val title: TextView = itemView.findViewById(R.id.info_title)
        val description: TextView = itemView.findViewById(R.id.info_description)
        val image: ImageView = itemView.findViewById(R.id.info_image)
        // 이름 , 설명은 현재 아이템의 변수를 각각 text에 저장하고
        // 이미지의 경우는 text에 해당하는 url을 연결하여
        // 이미지를 가져옴
        title.text = newsItem?.title ?: ""
        description.text = newsItem?.description ?: ""
        // 이미지 URL을 가져와서 Glide를 이용하여 이미지를 표시
        val imageUrl = newsItem?.image
        Glide.with(context)
            // 이미지 로딩
            .load(imageUrl)
            .into(image)

        return itemView
    }
    fun filterNewsByCategory(category: Int) {
        // 선택된 카테고리에 해당하는 뉴스만 필터링
        val filteredNewsList = newsDtoList.filter {
            it.category == category
        }
        // 어댑터에 필터링된 뉴스 목록 설정
        clear()
        // 필터링된 5개의 기사를 add
        addAll(filteredNewsList)
        notifyDataSetChanged()
    }

}