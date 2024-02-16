package com.a503.onjeong.domain.videocall.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.a503.onjeong.R
import com.a503.onjeong.databinding.ActivityGroupSelectBinding
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.mypage.api.GroupApiService
import com.a503.onjeong.domain.mypage.dto.GroupDTO
import com.a503.onjeong.domain.videocall.adapter.GroupSelectAdapter
import com.a503.onjeong.global.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GroupSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupSelectBinding
    private lateinit var adapter: GroupSelectAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.button.setOnClickListener {
//            getVideoCallToken()
//        }
        askForPermissions()

        getGroupList()

        binding.include.mainText.text = "영상통화"
        binding.include.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        binding.include.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    private fun getGroupList() {
        val retrofit = RetrofitClient.getApiClient(this)
        val service = retrofit.create(GroupApiService::class.java)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", 0L)
        val res = service.groupList(userId)

        res.enqueue(object : Callback<List<GroupDTO>> {
            override fun onResponse(
                call: Call<List<GroupDTO>>,
                response: Response<List<GroupDTO>>
            ) {
                val groupList = response.body() ?: emptyList()
                println(groupList.size)
                adapter = GroupSelectAdapter(
                    this@GroupSelectActivity,
                    R.layout.activity_group_list_item,
                    groupList
                )
                binding.groupListView.adapter = adapter
            }

            override fun onFailure(call: Call<List<GroupDTO>>, t: Throwable) {
                Log.e("Group", "Request failed: ${t.message}")
            }
        })

        binding.groupListView.setOnItemClickListener { parent, view, position, id ->
            val selectedGroup = adapter.getItem(position)?.groupId
            val intent = Intent(this, UserSelectActivity::class.java)
            intent.putExtra("selectedGroup", selectedGroup)
            startActivity(intent)
        }
    }

    fun askForPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                102
            )
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                101
            )
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }
}