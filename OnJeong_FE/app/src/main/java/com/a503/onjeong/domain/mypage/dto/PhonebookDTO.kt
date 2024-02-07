package com.a503.onjeong.domain.mypage.dto

import lombok.Builder
import lombok.Data


@Data
class PhonebookDTO @Builder constructor(
     val userId: Long,
     val freindId: Long,
     val phonebookNum: String,
     val phonebookName: String,
     var isChecked: Int,
     var profileUrl:String
)


