package com.a503.onjeong.domain.mypage.dto

import lombok.Data


@Data
class PhonebookAllDTO {
    var userId: Long? = null
    var phonebook: Map<String, String> = HashMap()
}

