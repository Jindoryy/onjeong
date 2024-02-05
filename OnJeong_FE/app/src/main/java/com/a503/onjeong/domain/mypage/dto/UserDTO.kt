package com.a503.onjeong.domain.mypage.dto

import lombok.Builder
import lombok.Data


@Data
class UserDTO @Builder constructor(
    private val userId: Long,
    private val name: String,
    private val phoneNumber: String
) {
    fun getName(): String {
        return name
    }
}

