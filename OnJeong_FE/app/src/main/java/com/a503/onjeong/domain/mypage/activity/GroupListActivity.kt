package com.a503.onjeong.domain.mypage.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.mypage.adapter.GroupListAdapter
import com.a503.onjeong.domain.mypage.api.GroupApiService
import com.a503.onjeong.domain.mypage.dto.GroupDTO
import com.a503.onjeong.global.network.RetrofitClient

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GroupListActivity : AppCompatActivity() {
    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private lateinit var groupListView: ListView
    private lateinit var adapter: GroupListAdapter
    private lateinit var groupAddBtn: Button
    private lateinit var mainText: TextView

    // NetRetrofit을 생성
    val retrofit = RetrofitClient.getApiClient(this)

    // NetRetrofit의 service를 통해 호출
    val service = retrofit.create(GroupApiService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)
        groupListView = findViewById(R.id.groupListView)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", 0L)
        val res = service.groupList(userId)
        // response가 null이 아니면 enqueue 호출
        if (res != null) {
            res.enqueue(object : Callback<List<GroupDTO>> {
                override fun onResponse(

                    call: Call<List<GroupDTO>>,
                    response: Response<List<GroupDTO>>
                ) {
                    val groupList = response.body() ?: emptyList()

                    adapter = GroupListAdapter(
                        this@GroupListActivity,
                        R.layout.activity_group_list_item,
                        groupList
                    )
                    groupListView.adapter = adapter
                }

                override fun onFailure(call: Call<List<GroupDTO>>, t: Throwable) {
                    Log.e("Group", "Request failed: ${t.message}")
                }
            })
        }

        //그룹 누르면 다른 액티비티로 가게
        groupListView.setOnItemClickListener { parent, view, position, id ->
            val selectedGroup = adapter.getItem(position)?.groupId?.toString()
            val intent = Intent(this, GroupDetailActivity::class.java)
            intent.putExtra("selectedGroup", selectedGroup)
            startActivity(intent)
        }
        //그룹 생성 버튼
        groupAddBtn = findViewById(R.id.groupAddBtn)
        groupAddBtn.setOnClickListener() {
            val intent = Intent(this, GroupCreateActivity::class.java)
            startActivity(intent)
        }

        // 홈버튼 누르면 홈으로 이동하게
        homeButton = findViewById(R.id.btnHome)
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        // 뒤로가기 버튼 누르면 뒤로(메인)이동
        backButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        mainText = findViewById(R.id.mainText)
        mainText.text = "모임 관리"

    }
}


