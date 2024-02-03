package com.a503.onjeong.domain.mypage.dto


import lombok.Builder
import lombok.Data


@Data
class GroupDTO @Builder constructor(
     val groupId: Long,
    private val ownerId: Long,
     val name: String,
    userList: List<UserDTO>
) {
     var userList: List<UserDTO> = ArrayList()
    init {
        this.userList = userList
    }
}


