package com.a503.onjeong.domain.videocall.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.a503.onjeong.R
import com.a503.onjeong.databinding.ActivityUserSelectBinding
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.mypage.api.GroupApiService
import com.a503.onjeong.domain.mypage.dto.GroupDTO
import com.a503.onjeong.domain.mypage.dto.UserDTO
import com.a503.onjeong.domain.videocall.adapter.GroupUserAdapter
import com.a503.onjeong.domain.videocall.adapter.SelectedUserAdapter
import com.a503.onjeong.domain.videocall.api.VideoCallApiService
import com.a503.onjeong.domain.videocall.dto.CallRequestDto
import com.a503.onjeong.domain.videocall.dto.SessionIdRequestDto
import com.a503.onjeong.global.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSelectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserSelectBinding
    private val selectedUserList = mutableListOf<UserDTO>()

    private lateinit var userAdapter: GroupUserAdapter
    private lateinit var selectedUserAdapter: SelectedUserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)


        getGroupMemberList()

        binding.include.mainText.text = "영상통화"
        binding.include.btnBack.setOnClickListener {
            val intent = Intent(this, GroupSelectActivity::class.java)
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

        binding.callButton.setOnClickListener {
            if (selectedUserList.size == 0) return@setOnClickListener
            getVideoCallToken()
        }
    }

    private fun getGroupMemberList() {
        val groupId = intent.getLongExtra("selectedGroup", 0L)

        val retrofit = RetrofitClient.getApiClient(this)
        val groupApiService = retrofit.create(GroupApiService::class.java)

        //groupId로 그룹 정보 가져오기
        val res = groupApiService.groupDetail(groupId)
        res.enqueue(object : Callback<GroupDTO> {
            override fun onResponse(
                call: Call<GroupDTO>,
                response: Response<GroupDTO>
            ) {
                val groupDTO: GroupDTO? = response.body()
                val userList: List<UserDTO>? = groupDTO?.userList

                if (userList != null) {
                    initRecycler(userList)
                }

                //그룹 이름 보이게 해야 함!!

//                val groupName: EditText = findViewById(R.id.groupName)
//                val editableText = Editable.Factory.getInstance().newEditable(groupDTO!!.name)
//                groupName.text = editableText

            }

            override fun onFailure(call: Call<GroupDTO>, t: Throwable) {
                Log.e("Group", "Request failed: ${t.message}")
            }
        })
    }

    private fun initRecycler(userList: List<UserDTO>) {
        userAdapter = GroupUserAdapter(userList) { selectedUser ->
            onUserSelected(selectedUser)
        }
        binding.userListView.adapter = userAdapter
        binding.userListView.layoutManager = GridLayoutManager(this, 2)

        selectedUserAdapter = SelectedUserAdapter(selectedUserList)
        binding.selectedUserListView.adapter = selectedUserAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.selectedUserListView.layoutManager = linearLayoutManager

    }

    private fun onUserSelected(selectedUser: UserDTO) {
        if (selectedUserList.contains(selectedUser)) {
            // 이미 선택된 사용자인 경우 제거
            selectedUserList.remove(selectedUser)
        } else {
            // 선택되지 않은 경우 추가
            selectedUserList.add(selectedUser)
        }

        selectedUserAdapter.notifyDataSetChanged()

        binding.callButton.isEnabled = selectedUserList.isNotEmpty()
        binding.callButton.backgroundTintList =
            if (binding.callButton.isEnabled) {ColorStateList.valueOf(getColor(R.color.main_color))
            } else ColorStateList.valueOf(
                getColor(R.color.check_gray)
            )
        binding.callButton.setTextColor(
            if (binding.callButton.isEnabled) {
                getColor(R.color.black)
            } else {
                getColor(R.color.white)
            }
        )

    }

    private fun getVideoCallToken() {
        val userId =
            getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE).getLong("userId", 0L)
        if (userId == 0L) {
            Log.e("VideoCall Log", "유저 정보 없음")
            return
        }

        val retrofit = RetrofitClient.getApiClient(this)

        val service = retrofit.create(VideoCallApiService::class.java)
        val call = service.createSessionId(SessionIdRequestDto(userId))

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val sessionId: String = response.body()!!.string()

                    sendCallRequest(sessionId)

                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("videoCall", "Request failed: ${t.message}")
            }
        })
    }

    private fun sendCallRequest(sessionId: String) {
        val name =
            getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE).getString("name", "")
                .toString()
        val userIdList: List<Long> = selectedUserList.map { it.getUserId() }
        val callRequestDto = CallRequestDto(userIdList, sessionId, name)


        val retrofit = RetrofitClient.getApiClient(this)

        val service = retrofit.create(VideoCallApiService::class.java)
        val call = service.sendCallRequest(callRequestDto)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@UserSelectActivity, VideoCallActivity::class.java)
                    intent.putExtra("sessionId", sessionId)
                    startActivity(intent)
//                    finish()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })

    }

}