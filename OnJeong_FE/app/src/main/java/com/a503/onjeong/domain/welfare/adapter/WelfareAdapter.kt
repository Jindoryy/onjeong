package com.a503.onjeong.domain.welfare.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.a503.onjeong.R
import com.a503.onjeong.domain.welfare.dto.WelfareData

class WelfareAdapter(private var welfareDataList: List<WelfareData>) :
    RecyclerView.Adapter<WelfareViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelfareViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_welfare_list, parent, false)
        return WelfareViewHolder(view)
    }

    override fun onBindViewHolder(holder: WelfareViewHolder, position: Int) {
        val welfareData = welfareDataList[position]
        holder.bind(welfareData)

        holder.itemView.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(welfareData.detailUrl)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return welfareDataList.size
    }

    fun submitList(newList: List<WelfareData>) {
        welfareDataList = newList
        notifyDataSetChanged()
    }
}


class WelfareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
    private val contentTextView: TextView = itemView.findViewById(R.id.textViewContent)
    fun bind(data: WelfareData) {
        titleTextView.text = data.supportTarget
        contentTextView.text = data.supportContent
    }
}
