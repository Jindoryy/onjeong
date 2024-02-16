package com.a503.onjeong.domain.mypage.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.login.activity.LoginActivity
import com.a503.onjeong.domain.mypage.api.PhonebookApiService
import com.a503.onjeong.domain.mypage.api.ProfileApiService
import com.a503.onjeong.domain.mypage.dto.PhonebookAllDTO
import com.a503.onjeong.domain.mypage.dto.UserDTO
import com.a503.onjeong.global.network.RetrofitClient
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyPageActivity : AppCompatActivity() {
    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private lateinit var userProfile: CircleImageView
    private lateinit var logoutButton: Button
    private lateinit var mainText: TextView

    val retrofit = RetrofitClient.getApiClient(this)
    val phonebookApiService = retrofit.create(PhonebookApiService::class.java)
    val profileApiService = retrofit.create(ProfileApiService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val userId = sharedPreferences.getLong("userId", 0L)


        val call = profileApiService.getUserInfo(userId);
        if (call != null) {
            call.enqueue(object : Callback<UserDTO?> {
                override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                    var userDTO: UserDTO? = response.body();
                    var name: TextView = findViewById(R.id.name)
                    if (userDTO != null) {
                        name.text = userDTO.getName()
                    }
                    var userProfile: CircleImageView = findViewById(R.id.userProfile)
                    // Glide로 이미지 표시하기
                    val imageUrl =
                        userDTO?.profileUrl
                    Glide.with(this@MyPageActivity).load(imageUrl).into(userProfile)


                }

                override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                    Log.e("Profile", "Request failed: ${t.message}")
                }
            })
        }
        val groupSettingBtn: Button = findViewById(R.id.groupSettingBtn)
        groupSettingBtn.setOnClickListener {
            val intent = Intent(this@MyPageActivity, GroupListActivity::class.java)
            startActivity(intent)
        }

        val phoneBookBtn: Button = findViewById(R.id.phoneBookBtn)
        phoneBookBtn.setOnClickListener {
            //연락처 동기화
            //퍼미션 허용했는지 확인 후 데이터 전송
            checkPermission()
        }
        //전화번호 수정
        val phoneNumEditBtn: Button = findViewById(R.id.phoneNumEditBtn)
        phoneNumEditBtn.setOnClickListener {
            val intent = Intent(this@MyPageActivity, TelephoneEditActivity::class.java)
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
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        mainText = findViewById(R.id.mainText)
        mainText.text = "내 정보"

        //프로필사진 클릭시 갤러리 접근
        //삭제 수정 나와야하나
        userProfile = findViewById(R.id.userProfile)
        userProfile.setOnClickListener {
            val intent = Intent(this, ProfileSettingActivity::class.java)
            startActivity(intent)
        }

        logoutButton = findViewById(R.id.logoutBtn)
        logoutButton.setOnClickListener {
            editor.remove("kakaoAccessToken")
            editor.remove("kakaoRefreshToken")
            editor.remove("jwtAccessToken")
            editor.remove("jwtRefreshToken")
            editor.remove("userId")
            editor.remove("name")
            editor.remove("type")
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // 권한을 확인하고 연락처를 가져오는 함수
    private fun checkPermission() {
        // 사용자에게 연락처 읽기 권한이 있는지 확인
        val status =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)

        if (status == PackageManager.PERMISSION_GRANTED) {

            // 권한이 이미 허용되어 있을 때의 처리
            // 연락처를 가져와서 서버에 전송하는 함수 호출
            sendPhonebook()
        } else {
            // 권한이 없을 때 권한 요청 다이얼로그 표시
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                100
            )
        }
    }

    @SuppressLint("Range")
    private fun sendPhonebook() {
        val cr = contentResolver
        val cur =
            cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        val phonebookAllDTO = PhonebookAllDTO()
        val phonebook: MutableMap<String, String> = mutableMapOf();

        if (cur!!.count > 0) {
            var line = ""
            while (cur!!.moveToNext()) {
                var phoneNumber = ""
                val id =
                    cur!!.getInt(cur!!.getColumnIndex(ContactsContract.Contacts._ID))
                line = String.format("%4d", id)
                val name =
                    cur!!.getString(cur!!.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                line += " $name"
                if ("1" == cur!!.getString(cur!!.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        arrayOf(id.toString()),
                        null
                    )
                    var i = 0
                    val pCount = pCur!!.count
                    val phoneNum = arrayOfNulls<String>(pCount)
                    val phoneType = arrayOfNulls<String>(pCount)
                    while (pCur!!.moveToNext()) {
                        phoneNum[i] =
                            pCur!!.getString(pCur!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        line += " " + phoneNum[i]
                        phoneNumber = phoneNum[i].toString()
                        phoneNumber = phoneNumber.replace("-", "");
                        phoneType[i] =
                            pCur!!.getString(pCur!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                        i++
                    }
                }

                phonebook.put(phoneNumber, name);
            }
        }
        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", 0L)
        phonebookAllDTO.phonebook = phonebook;
        phonebookAllDTO.userId = userId;
        //객체 보내기
        val call = phonebookApiService.phonebookSave(phonebookAllDTO)
        if (call != null) {
            call.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    Toast.makeText(applicationContext, "연락처를 불러왔습니다", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Log.e("PhoneBook", "Request failed: ${t.message}")
                }
            })
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            100 -> {
                if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 사용자가 권한을 부여한 경우
                    Log.d("test", "permission granted")
                    // 연락처를 가져와서 서버에 전송하는 함수 호출
                } else {
                    // 사용자가 권한을 거부한 경우
                    Log.d("test", "permission denied")
                    // 권한을 거부한 상황에 대한 처리를 추가
                }
            }
        }
    }


}