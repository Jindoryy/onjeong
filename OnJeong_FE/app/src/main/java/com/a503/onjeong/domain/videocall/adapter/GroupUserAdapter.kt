package com.a503.onjeong.domain.videocall.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.a503.onjeong.R
import com.a503.onjeong.domain.mypage.dto.UserDTO
import com.bumptech.glide.Glide

class GroupUserAdapter(
    private val userList: List<UserDTO>,
    private val onItemClick: (UserDTO) -> Unit
) :
    RecyclerView.Adapter<GroupUserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupUserAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: GroupUserAdapter.ViewHolder, position: Int) {
//        holder.bindItems(userList[position])
        val user = userList[position]
        holder.bindItems(user)



        holder.itemView.setOnClickListener {
            // 사용자를 선택했을 때 호출되는 콜백
            onItemClick(user)
            // 선택 여부 업데이트
            holder.changeSelected(!holder.isSelected)
        }
    }

    override fun getItemCount(): Int {
        return userList.count()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var isSelected: Boolean = false
        fun bindItems(user: UserDTO) {
            val userName = itemView.findViewById<TextView>(R.id.user_name)
            val userImage = itemView.findViewById<ImageView>(R.id.user_image)
            Glide.with(itemView).load(user.profileUrl).into(userImage)
            userName.text = user.getName()
            changeSelected(isSelected)
        }

        fun changeSelected(selected: Boolean) {
            isSelected = selected
            //선택 여부에 따라 색 변화
            val selectImage = itemView.findViewById<ImageView>(R.id.select_image)
            if (isSelected) {
                val colorStateList =
                    ContextCompat.getColorStateList(itemView.context, R.color.main_color)
                selectImage.imageTintList = colorStateList
            } else {
                val colorStateList =
                    ContextCompat.getColorStateList(itemView.context, R.color.check_gray)
                selectImage.imageTintList = colorStateList
            }
        }
    }
}