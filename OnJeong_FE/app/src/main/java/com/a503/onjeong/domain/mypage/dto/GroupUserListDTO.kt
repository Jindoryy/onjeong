package com.a503.onjeong.domain.mypage.dto

import lombok.Builder
import lombok.Data


@Data
class GroupUserListDTO @Builder constructor(
    private val groupId: Long,
    private val ownerId: Long,
    private val name: String,
    userList: List<Long>
) {
    private var userList: List<Long> = ArrayList<Long>()

    init {
        this.userList = userList
    }
}

