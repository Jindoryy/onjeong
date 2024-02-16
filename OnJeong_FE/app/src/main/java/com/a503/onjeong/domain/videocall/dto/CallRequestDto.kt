package com.a503.onjeong.domain.videocall.dto

data class CallRequestDto(
    val userIdList: List<Long>,
    val sessionId: String,
    val callerName: String
)
