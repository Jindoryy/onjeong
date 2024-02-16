package com.a503.onjeong.domain.videocall.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.a503.onjeong.R
import com.a503.onjeong.domain.mypage.dto.GroupDTO

class GroupSelectAdapter(context: Context, resource: Int, objects: List<GroupDTO>) :
    ArrayAdapter<GroupDTO>(context, resource, objects) {
    private lateinit var adapter: GroupSelectAdapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = convertView ?: inflater.inflate(R.layout.activity_group_list_item, parent, false)

        val groupItem = getItem(position)
        val name: TextView = itemView.findViewById(R.id.groupName)
        if (groupItem != null) {
            name.text = groupItem.name
        }
        return itemView
    }

}