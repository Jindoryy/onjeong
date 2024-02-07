package com.a503.onjeong.domain.mypage.dto

import lombok.Builder
import lombok.Data


@Data
class UserDTO @Builder constructor(
    private val userId: Long,
     val name: String,
     val phoneNumber: String,
     val profileUrl: String
)

