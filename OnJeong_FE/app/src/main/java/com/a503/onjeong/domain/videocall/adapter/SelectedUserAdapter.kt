package com.a503.onjeong.domain.videocall.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a503.onjeong.R
import com.a503.onjeong.domain.mypage.dto.UserDTO
import com.bumptech.glide.Glide

class SelectedUserAdapter(private val selectedUserList: List<UserDTO>) :
    RecyclerView.Adapter<SelectedUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_check_list_item, parent, false)
        return SelectedUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: SelectedUserViewHolder, position: Int) {
        val selectedUser = selectedUserList[position]
        holder.bind(selectedUser)
    }

    override fun getItemCount(): Int {
        return selectedUserList.size
    }
}

// SelectedUserViewHolder.kt
class SelectedUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(selectedUser: UserDTO) {
        // 선택된 사용자 정보를 뷰에 설정하는 로직
        val userName = itemView.findViewById<TextView>(R.id.userName)

        val profileImg = itemView.findViewById<ImageView>(R.id.profileImg)

        Glide.with(itemView).load(selectedUser.profileUrl).into(profileImg)
        userName.text = selectedUser.getName()
    }
}