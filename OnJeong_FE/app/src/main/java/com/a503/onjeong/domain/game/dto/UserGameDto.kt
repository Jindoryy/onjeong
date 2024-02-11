package com.a503.onjeong.domain.game.dto

data class UserGameDto(
    val userId: Long,
    val gameId: Long,
    val userGameScore: Long
)