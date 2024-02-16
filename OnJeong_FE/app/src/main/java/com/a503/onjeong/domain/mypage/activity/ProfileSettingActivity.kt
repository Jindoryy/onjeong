package com.a503.onjeong.domain.mypage.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.mypage.api.ProfileApiService
import com.a503.onjeong.global.network.RetrofitClient
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProfileSettingActivity : AppCompatActivity() {
    private lateinit var homeButton: Button
    private lateinit var backButton: Button
    private lateinit var mainText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setting)
        val selectImg: LinearLayout = findViewById(R.id.selectImg)
        selectImg.setOnClickListener() {
            selectProfileImg()
        }

        val deleteImg: LinearLayout = findViewById(R.id.deleteImg)
        deleteImg.setOnClickListener() {
            deleteProfileImg()
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
        mainText.text = "사진 관리"

    }

    fun deleteProfileImg() {
        Log.d("delete", "delete")
        // NetRetrofit을 생성
        val retrofit = RetrofitClient.getApiClient(this)

        // NetRetrofit의 service를 통해 호출
        val service = retrofit.create(ProfileApiService::class.java)

        val sharedPreferences = getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", 0L)
        val call = service.profileDelete(userId);
        if (call != null) {
            call.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {

                    val intent = Intent(this@ProfileSettingActivity, MyPageActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Log.e("Profile", "Request failed: ${t.message}")
                }
            })
        }
//        val intent = Intent(this, MyPageActivity::class.java)
//        startActivity(intent)

    }

    //사진첩에서 사진선택
    fun selectProfileImg() {
        Log.d("select", "select")
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigatePhotos()
                //스토리지 읽기 권한이 허용이면 커스텀 앨범 띄워주기
                //권한 있을 경우 : PERMISSION_GRANTED
                //권한 없을 경우 : PERMISSION_DENIED
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                //권한을 명시적으로 거부한 경우 : ture
                //다시 묻지 않음을 선택한 경우 : false
                //다이얼로그를 띄워 권한 팝업을 해야하는 이유 및 권한팝업을 허용하여야 접근 가능하다는 사실을 알려줌
                showPermissionAlertDialog()
            }

            else -> {
                //권한 요청
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    101
                )
            }
        }
    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 10000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            10000 -> {
                data ?: return
                val uri = data.data as Uri
                val file = File(getRealPathFromURI(this, uri))
                val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                val body = MultipartBody.Part.createFormData("profile", file.name, requestFile)

                val sharedPreferences =
                    getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getLong("userId", 0L)

                val retrofit = RetrofitClient.getApiClient(this)
                val service = retrofit.create(ProfileApiService::class.java)

                val call = service.profileUpdate(userId, body);
                if (call != null) {
                    call.enqueue(object : Callback<Void?> {
                        override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                            val intent =
                                Intent(this@ProfileSettingActivity, MyPageActivity::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        }

                        override fun onFailure(call: Call<Void?>, t: Throwable) {
                            Log.e("Profile", "Request failed: ${t.message}")
                        }
                    })
                }
            }

            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 절대경로 변환
    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val wholeID = DocumentsContract.getDocumentId(uri)
        val id = wholeID.split(":")[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column,
            sel,
            arrayOf(id),
            null
        )
        val columnIndex = cursor?.getColumnIndex(column[0])
        if (cursor != null && columnIndex != null && cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
            cursor.close()
        }
        return filePath
    }

    private fun showPermissionAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 승인이 필요합니다.")
            .setMessage("사진을 선택 하시려면 권한이 필요합니다.")
            .setPositiveButton("허용하기") { _, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    101
                )
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            101 -> {
                // 위치 권한에 대한 요청 결과 처리
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //권한 허용클릭
                    navigatePhotos()
                } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    //권한 처음으로 거절 했을 경우
                    //한번더 권한 요청
                    showPermissionAlertDialog()
                } else {
                    //권한 두번째로 거절 한 경우 (다시 묻지 않음)
                    //설정 -> 권한으로 이동하는 다이얼로그
                    goSettingActivityAlertDialog()
                }
            }
        }
    }

    private fun goSettingActivityAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 승인이 필요합니다.")
            .setMessage("앨범에 접근 하기 위한 권한이 필요합니다.\n권한 -> 저장공간 -> 허용")
            .setPositiveButton("허용하러 가기") { _, _ ->
                val goSettingPermission = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                goSettingPermission.data = Uri.parse("package:$packageName")
                startActivity(goSettingPermission)
            }
            .setNegativeButton("취소") { _, _ -> }
            .create()
            .show()
    }
}