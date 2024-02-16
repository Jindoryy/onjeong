package com.a503.onjeong.domain.counselor.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import com.a503.onjeong.R
import com.a503.onjeong.domain.MainActivity
import com.a503.onjeong.domain.counselor.api.CreateApiService
import com.a503.onjeong.domain.counselor.openvidu.LocalParticipant
import com.a503.onjeong.domain.counselor.openvidu.RemoteParticipant
import com.a503.onjeong.domain.counselor.openvidu.Session
import com.a503.onjeong.domain.counselor.utils.CustomHttpClient
import com.a503.onjeong.domain.counselor.websocket.CustomWebSocket
import com.a503.onjeong.global.network.RetrofitClient.getApiClient
import okhttp3.ResponseBody
import org.webrtc.EglBase
import org.webrtc.MediaStream
import org.webrtc.SurfaceViewRenderer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CounselorActivity : AppCompatActivity() {
    private val TAG = "CounselorActivity"
    var views_container: LinearLayout? = null
    var start_finish_call: ImageButton? = null
    var session_name: EditText? = null
    var participant_name: EditText? = null
    var application_server_url: EditText? = null
    var localVideoView: SurfaceViewRenderer? = null
    var main_participant: TextView? = null
    var peer_container: FrameLayout? = null

    private val APPLICATION_SERVER_URL: String? = null
    private var session: Session? = null
    private val httpClient: CustomHttpClient? = null
    private var sessionId: String? = null
    private var token: String? = null
    private var userId: String? = null
    private var room_user_id: String? = null

    // 액티비티가 처음 생성될때 호출, 초기 설정 데이터 로딩
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT // 화면 방향 세로 방향 고정
        setContentView(R.layout.activity_counselor) // 액티비티 레이아웃 설정 부분
        views_container = findViewById(R.id.views_container)
        start_finish_call = findViewById(R.id.start_finish_call)

        localVideoView = findViewById(R.id.local_gl_surface_view)
        main_participant = findViewById(R.id.main_participant)
        peer_container = findViewById(R.id.peer_container)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        askForPermissions()
        ButterKnife.bind(this)
        token = intent.getStringExtra("token")
        sessionId = intent.getStringExtra("sessionId")
        userId = intent.getStringExtra("userId")
        println("token: $token")
        println("sessionId: $sessionId")
        initViews()
        viewToConnectingState()
        getTokenSuccess(token, sessionId)

        //상담원 아이디 받아오기
        val retrofit = getApiClient(this)
        val service = retrofit.create(CreateApiService::class.java)
        val call2 = service.getRoomUserId(sessionId!!)
        call2.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    room_user_id = response.body()!!.string()
                    Log.d(TAG, "Get Room for UserId success")
                } else {
                    Log.e(TAG, "Failed to get UserId: " + response.code())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e(TAG, "Failed to get", t)
            }
        })

    }

    // 카메라 및 오디오 녹음 권한 요청, 권한이 없을 때만 요청
    fun askForPermissions() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_REQUEST
            ) // 요청결과 -> 상수
        } else if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.RECORD_AUDIO),
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
            )
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        }
    }

    //상담 방 나가기
    fun buttonPressed(view: View?) {

        leaveSession()
        return

    }

    //모든 유저 토큰 성공시 실행
    private fun getTokenSuccess(token: String?, sessionId: String?) {
        // Initialize our session
        session = Session(sessionId, token, views_container, this)

        //로컬 참여자 초기 실행 및 카메라 연결
        val participantName =
            getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE).getString("name", "")
                .toString()
        val localParticipant =
            LocalParticipant(participantName, session, this.applicationContext, localVideoView)
        localParticipant.startCamera()
        runOnUiThread {

            //사용자 화면 초기화
//            main_participant!!.text = participant_name!!.text.toString()
//            main_participant!!.setPadding(20, 3, 20, 3)
        }
        startWebSocket()
    }

    private fun startWebSocket() {
        val webSocket = CustomWebSocket(session, this)
        webSocket.execute()
        session!!.setWebSocket(webSocket)
    }

    private fun connectionError(url: String) {
        val myRunnable = Runnable {
            val toast = Toast.makeText(this, "Error connecting to $url", Toast.LENGTH_LONG)
            toast.show()
            viewToDisconnectedState()
        }
        Handler(this.mainLooper).post(myRunnable)
    }

    //상담화면 초기화
    private fun initViews() {
        //OpenGL ES에서 EGL(OpenGL for Embedded Systems) 컨텍스트를 나타내는데 사용
        val rootEglBase = EglBase.create()
        localVideoView!!.init(rootEglBase.eglBaseContext, null)
        localVideoView!!.setMirror(true)
        localVideoView!!.setEnableHardwareScaler(true)
        //다른 뷰 위에 비디오 뷰를 표시하도록 하는 옵션
        localVideoView!!.setZOrderMediaOverlay(true)
    }

    fun viewToDisconnectedState() {
        runOnUiThread {
            localVideoView!!.clearImage()
            localVideoView!!.release()
            start_finish_call!!.isEnabled = true
//            application_server_url!!.isEnabled = true
//            application_server_url!!.isFocusableInTouchMode = true
//            session_name!!.isEnabled = true
//            session_name!!.isFocusableInTouchMode = true
//            participant_name!!.isEnabled = true
//            participant_name!!.isFocusableInTouchMode = true
            main_participant!!.setText(null)
            main_participant!!.setPadding(0, 0, 0, 0)
        }
    }

    fun viewToConnectingState() {
        runOnUiThread {
            start_finish_call!!.isEnabled = false
//            application_server_url!!.isEnabled = false
//            application_server_url!!.isFocusable = false
//            session_name!!.isEnabled = false
//            session_name!!.isFocusable = false
//            participant_name!!.isEnabled = false
//            participant_name!!.isFocusable = false
        }
    }

    fun viewToConnectedState() {
        runOnUiThread {
            start_finish_call!!.isEnabled = true
        }
    }

    fun createRemoteParticipantVideo(remoteParticipant: RemoteParticipant) {
        val mainHandler = Handler(this.mainLooper)
        val myRunnable = Runnable {
            val rowView = this.layoutInflater.inflate(R.layout.peer_video, null)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 20)
            rowView.layoutParams = lp
            val rowId = View.generateViewId()
            rowView.id = rowId
            views_container!!.addView(rowView)
            val videoView = (rowView as ViewGroup).getChildAt(0) as SurfaceViewRenderer
            remoteParticipant.videoView = videoView
            videoView.setMirror(false)
            val rootEglBase = EglBase.create()
            videoView.init(rootEglBase.eglBaseContext, null)
            videoView.setZOrderMediaOverlay(true)
            val textView = rowView.getChildAt(1)
            remoteParticipant.participantNameText = textView as TextView
            remoteParticipant.view = rowView
            remoteParticipant.participantNameText.text = remoteParticipant.participantName
            remoteParticipant.participantNameText.setPadding(20, 3, 20, 3)
        }
        mainHandler.post(myRunnable)
    }

    fun setRemoteMediaStream(stream: MediaStream, remoteParticipant: RemoteParticipant) {
        val videoTrack = stream.videoTracks[0]
        videoTrack.addSink(remoteParticipant.videoView)
        runOnUiThread { remoteParticipant.videoView.visibility = View.VISIBLE }
    }

    fun leaveSession() {
        if (session != null) {
            session!!.leaveSession()
        }
        if (httpClient != null) {
            httpClient.dispose()
        }
        viewToDisconnectedState()

        val retrofit = getApiClient(this)
        val service = retrofit.create(CreateApiService::class.java)

        //상담원이 방을 나갈때
        if (userId == room_user_id) {
            val call = service.deleteRoom(sessionId!!)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Room deleted successfully")
                        val intent = Intent(this@CounselorActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 Activity 종료
                    } else {
                        Log.e(TAG, "Failed to delete room: " + response.code())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Failed to delete room", t)
                }
            })
        }
        //클라이언트가 방을 나갈때
        else {
            val call2 = service.userDisconnect(sessionId!!)
            call2.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "Room out successfully")
                        val intent = Intent(this@CounselorActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() // 현재 Activity 종료
                    } else {
                        Log.e(TAG, "Failed to out room: " + response.code())
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "Failed to delete room", t)
                }
            })
        }

        // MainActivity로 돌아가기

    }

    private fun arePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_DENIED
    }

    override fun onDestroy() {
        leaveSession()
        super.onDestroy()
    }

    override fun onBackPressed() {
        leaveSession()
        super.onBackPressed()
    }

    override fun onStop() {
        leaveSession()
        super.onStop()
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_CAMERA = 100
        private const val MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 101
        private const val MY_PERMISSIONS_REQUEST = 102
    }
}
