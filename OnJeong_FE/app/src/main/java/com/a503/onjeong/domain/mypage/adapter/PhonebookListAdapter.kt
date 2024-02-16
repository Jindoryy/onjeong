package com.a503.onjeong.domain.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.a503.onjeong.R
import com.a503.onjeong.domain.mypage.dialog.UserAlertDialog
import com.a503.onjeong.domain.mypage.dto.PhonebookDTO
import com.a503.onjeong.domain.mypage.listener.OnButtonClickListener
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView


class PhonebookListAdapter(
    context: Context,
    resource: Int,
    objects: List<PhonebookDTO>,
    private val onButtonClickListener: OnButtonClickListener
) :
    ArrayAdapter<PhonebookDTO>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView =
            convertView ?: inflater.inflate(R.layout.activity_phonebook_list_item, parent, false)

        val phonebookItem = getItem(position)
        val name: TextView = itemView.findViewById(R.id.userName)
        val checkBtn: Button = itemView.findViewById(R.id.checkBtn)
        val userDetailBtn: ImageButton = itemView.findViewById(R.id.userDetailBtn)
        var isChecked: Int = phonebookItem!!.isChecked;
        if (phonebookItem != null) {
            name.text = phonebookItem.phonebookName
            if (isChecked == 1) { //선택된 것과 안된 것 배경색 변화
                checkBtn.setBackgroundResource(R.color.main_color)
            } else {
                checkBtn.setBackgroundResource(R.color.check_gray)
            }
        }

        var userProfile: CircleImageView = itemView.findViewById(R.id.userProfile)
        // Glide로 이미지 표시하기
        Glide.with(itemView).load(phonebookItem.profileUrl).into(userProfile)


        // 어댑터에서 버튼에 대한 클릭 이벤트 핸들러 정의
        // 유저 상세보기
        userDetailBtn.setOnClickListener {
            val customDialog = UserAlertDialog(context, phonebookItem)
            customDialog.show()
        }

        //선택 버튼
        //이미 선택되어있다면 취소, 안되어있다면 선택 (0->1/1->0)
        checkBtn.setOnClickListener {
            isChecked = (isChecked + 1) % 2 // 상태를 반전시킴 (true면 false로, false면 true로)
            // isChecked에 따라 버튼의 배경색을 변경할 수 있습니다.
            if (isChecked == 1) {
                checkBtn.setBackgroundResource(R.color.main_color)
            } else {
                checkBtn.setBackgroundResource(R.color.check_gray)
            }
            phonebookItem.isChecked = isChecked
            onButtonClickListener.onButtonClick(phonebookItem)
        }
        return itemView
    }

}