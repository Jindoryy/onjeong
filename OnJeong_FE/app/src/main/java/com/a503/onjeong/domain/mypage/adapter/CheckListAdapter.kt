package com.a503.onjeong.domain.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a503.onjeong.R
import com.a503.onjeong.domain.mypage.dto.PhonebookDTO
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

//class CheckListAdapter(context: Context, resource: Int, objects: List<PhonebookDTO>) :
//    ArrayAdapter<PhonebookDTO>(context, resource, objects) {
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val itemView = convertView ?: inflater.inflate(R.layout.activity_check_list_item, parent, false)
//        val checkItem = getItem(position)
//        val name: TextView = itemView.findViewById(R.id.userName)
//        if (checkItem != null) {
//            name.text = checkItem.phonebookName
//        }
//
//        var profileImg: CircleImageView = itemView.findViewById(R.id.profileImg)
//        // Glide로 이미지 표시하기
//        if (checkItem != null) {
//            Glide.with(itemView).load(checkItem.profileUrl).into(profileImg)
//        }
//
//        return itemView
//    }
//}

class CheckListAdapter(private val context: Context, private val objects: List<PhonebookDTO>) :
    RecyclerView.Adapter<CheckListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.userName)
        val profileImg: CircleImageView = itemView.findViewById(R.id.profileImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.activity_check_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkItem = objects[position]

        holder.name.text = checkItem.phonebookName

        // Glide로 이미지 표시하기
        Glide.with(holder.itemView).load(checkItem.profileUrl).into(holder.profileImg)
    }

    override fun getItemCount(): Int {
        return objects.size
    }
}
