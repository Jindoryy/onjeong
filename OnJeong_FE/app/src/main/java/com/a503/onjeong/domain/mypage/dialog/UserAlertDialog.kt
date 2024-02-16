package com.a503.onjeong.domain.mypage.dialog

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.a503.onjeong.R
import com.a503.onjeong.domain.mypage.dto.PhonebookDTO
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UserAlertDialog(context: Context, phonebookItem: PhonebookDTO) : Dialog(context) {
    init {
        setContentView(R.layout.activity_alert_dialog)

        val name: TextView=findViewById(R.id.name2)
        name.text=phonebookItem.phonebookName

        val phoneNum:TextView=findViewById(R.id.phoneNum)
        phoneNum.text=phonebookItem.phonebookNum

        var profileImg: CircleImageView = findViewById(R.id.profileImg)
        // Glide로 이미지 표시하기
        Glide.with(context).load(phonebookItem.profileUrl).into(profileImg)
    }

}
