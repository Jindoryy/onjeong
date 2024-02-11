package com.a503.onjeong.domain.game.dto

data class UserGameResponseDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val gameId: Long,
    val userGameScore: Long
)
