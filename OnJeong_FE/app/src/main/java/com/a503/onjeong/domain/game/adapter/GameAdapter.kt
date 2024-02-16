package com.a503.onjeong.domain.game.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.a503.onjeong.R
import com.a503.onjeong.domain.game.dto.UserGameResponseDto

class GameAdapter(context: Context, resource: Int, objects: List<UserGameResponseDto>) :
    ArrayAdapter<UserGameResponseDto>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = convertView ?: inflater.inflate(R.layout.activity_game_rank_list, parent, false)
        // list에 해당하는 것들을 
        // rank list로 하나씩 배정
        val rankItem = getItem(position)
        val name: TextView = itemView.findViewById(R.id.rank_name)
        val score: TextView = itemView.findViewById(R.id.rank_score)
        val rankNo: TextView = itemView.findViewById(R.id.rank_no)

        rankNo.text = (position + 1).toString()
        name.text = rankItem?.userName ?: ""
        score.text = rankItem?.userGameScore.toString() ?: ""

        return itemView
    }



}