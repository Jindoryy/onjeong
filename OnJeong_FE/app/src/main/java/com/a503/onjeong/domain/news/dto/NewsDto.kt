package com.a503.onjeong.domain.news.dto

import javax.persistence.*

@Entity
data class NewsDto(
    val title : String,
    val category : Int,
    val description : String,
    val url : String,
    val image : String
)
