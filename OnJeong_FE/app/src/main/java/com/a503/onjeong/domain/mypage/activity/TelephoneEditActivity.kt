package com.a503.onjeong.domain.mypage.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.a503.onjeong.R
import com.a503.onjeong.domain.login.api.LoginApiService
import com.a503.onjeong.domain.mypage.api.ProfileApiService
import com.a503.onjeong.global.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TelephoneEditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)

        val editTextPhone = findViewById<EditText>(R.id.editTextPhone)
        val buttonAuth = findViewById<Button>(R.id.buttonAuth)
        val editTextAuthCode = findViewById<EditText>(R.id.editTextAuthCode)
        val buttonAuthenticate = findViewById<Button>(R.id.buttonAuthenticate)

        val retrofit = RetrofitClient.getAuthApiClient()

        // EditText에 텍스트 변경 감지 리스너 추가
        editTextPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // EditText의 텍스트가 변경될 때마다 호출되는 부분
                val phoneNumber = s.toString().trim()
                // 전화번호가 비어 있지 않은 경우 "인증" 버튼 활성화, 그렇지 않은 경우 비활성화
                buttonAuth.isEnabled = phoneNumber.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", 0L)

        // "인증" 버튼 클릭 시 수행할 동작 설정
        buttonAuth.setOnClickListener {
            val phoneNumber = editTextPhone.text.toString().trim()

            val loginService = retrofit.create(LoginApiService::class.java)
            val profileApiService = retrofit.create(ProfileApiService::class.java)

            val phoneCall = loginService.phone(phoneNumber)
            val updatePhoneNum = profileApiService.updatePhoneNum(userId, phoneNumber)


            phoneCall.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        // 인증 요청 버튼을 숨기고, 인증번호 입력란과 인증하기 버튼을 보이도록 설정
                        buttonAuth.visibility = View.INVISIBLE
                        editTextAuthCode.visibility = View.VISIBLE
                        buttonAuthenticate.visibility = View.VISIBLE

                        buttonAuthenticate.setOnClickListener {
                            // 버튼 클릭 시 실행되는 부분 (인증하기 버튼 클릭 시 처리)
                            val serverCode = response.body()
                            val userEnteredCode = editTextAuthCode.text.toString().trim()

                            // 인증 번호 검증
                            if (userEnteredCode == serverCode) {
                                Toast.makeText(
                                    this@TelephoneEditActivity,
                                    "인증 되었습니다.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                updatePhoneNum.enqueue(object : Callback<Void?> {
                                    override fun onResponse(
                                        call: Call<Void?>,
                                        response: Response<Void?>
                                    ) {

                                    }

                                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                                        Log.e("Telephone", "Request failed: ${t.message}")
                                    }
                                })
                            } else {
                                // 인증 실패, Toast 메시지로 "인증 실패" 표시
                                Toast.makeText(
                                    this@TelephoneEditActivity,
                                    "인증번호가 일치하지 않습니다. 다시 시도해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 다시 전화번호 입력란에 포커스를 주어 사용자가 편리하게 다시 입력할 수 있도록 함
                                editTextPhone.requestFocus()
                                buttonAuth.visibility = View.VISIBLE
                                editTextAuthCode.visibility = View.INVISIBLE
                                buttonAuthenticate.visibility = View.INVISIBLE
                            }
                        }
                    } else {
                        // 에러 처리를 여기에 추가
                        Log.e("Telephone", "전화번호 인증 오류")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // 실패 처리를 여기에 추가
                    Log.e("Telephone", "Request failed: ${t.message}")
                }
            })
        }

    }
}