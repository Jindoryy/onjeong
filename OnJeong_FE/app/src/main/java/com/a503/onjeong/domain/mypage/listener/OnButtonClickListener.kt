package com.a503.onjeong.domain.mypage.listener

import com.a503.onjeong.domain.mypage.dto.PhonebookDTO

interface OnButtonClickListener {
        fun onButtonClick(data: PhonebookDTO)
}