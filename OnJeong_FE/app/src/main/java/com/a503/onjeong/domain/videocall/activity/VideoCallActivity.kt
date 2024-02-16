package com.a503.onjeong.domain.videocall.activity

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.a503.onjeong.R
import com.a503.onjeong.databinding.ActivityVideoCallBinding
import com.a503.onjeong.domain.videocall.api.VideoCallApiService
import com.a503.onjeong.domain.videocall.fragments.PermissionsDialogFragment
import com.a503.onjeong.domain.videocall.openvidu.LocalParticipant
import com.a503.onjeong.domain.videocall.openvidu.RemoteParticipant
import com.a503.onjeong.domain.videocall.openvidu.Session
import com.a503.onjeong.domain.videocall.utils.CustomHttpClient
import com.a503.onjeong.domain.videocall.websocket.CustomWebSocket
import com.a503.onjeong.global.network.RetrofitClient
import okhttp3.ResponseBody
import org.webrtc.EglBase
import org.webrtc.MediaStream
import org.webrtc.SurfaceViewRenderer

class VideoCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoCallBinding
    private val TAG = "VideoCallActivity"
    private var APPLICATION_SERVER_URL: String? = null
    private var session: Session? = null
    private var httpClient: CustomHttpClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(binding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
//        askForPermissions()
        checkPermission()

        removeNotification()

        binding.finishCall.setOnClickListener {
            leaveSession()
            finish()
        }
    }

    private fun removeNotification() {
        val notificationId = intent.getIntExtra("notificationId", 0)
        // Cancel the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    private fun getConnection(sessionId: String) {
        val retrofit = RetrofitClient.getApiClient(this)

        val service = retrofit.create(VideoCallApiService::class.java)
        val call = service.createConnection(sessionId)

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val connectionToken: String = response.body()!!.string()
                    getTokenSuccess(connectionToken, sessionId)
//                    finish()
                } else {
                    Log.e("VideoCall", "get connection failed")
                }
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                Log.e("VideoCall", "Request failed: ${t.message}")

            }
        })
    }

//    fun askForPermissions() {
//        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                    != PackageManager.PERMISSION_GRANTED) &&
//            (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//                    != PackageManager.PERMISSION_GRANTED)
//        ) {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO),
//                MY_PERMISSIONS_REQUEST
//            )
//        } else if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.RECORD_AUDIO
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.RECORD_AUDIO),
//                MY_PERMISSIONS_REQUEST_RECORD_AUDIO
//            )
//        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this, arrayOf(Manifest.permission.CAMERA),
//                MY_PERMISSIONS_REQUEST_CAMERA
//            )
//        }
//    }

    fun checkPermission() {
        if (arePermissionGranted()) {
            initViews()
            viewToConnectingState()
//            APPLICATION_SERVER_URL = binding.applicationServerUrl.text.toString()
//            httpClient = CustomHttpClient(APPLICATION_SERVER_URL)
//            Log.d(TAG, "application server url is $APPLICATION_SERVER_URL")
//            val sessionId = binding.sessionName.text.toString()
//            Log.d(TAG, "session id is $sessionId")
//            getToken(sessionId)
            val sessionId = intent.getStringExtra("sessionId")
            if (sessionId == null) {
                leaveSession()
                finish()
            }
            getConnection(sessionId!!);
        } else {
            val permissionsFragment: DialogFragment = PermissionsDialogFragment()
            permissionsFragment.show(supportFragmentManager, "Permissions Fragment")
            finish()
        }
    }


    private fun getTokenSuccess(token: String?, sessionId: String) {
        // Initialize our session
        session = Session(sessionId, token, binding.viewsContainer, this)

        // Initialize our local participant and start local camera
//        val participantName = binding.participantName.text.toString()
        // 이름 변경
        val participantName =
            getSharedPreferences("mySharedPreferences", Context.MODE_PRIVATE).getString("name", "")
                .toString()
        val localParticipant =
            LocalParticipant(
                participantName,
                session,
                this.applicationContext,
                binding.localGlSurfaceView
            )
        localParticipant.startCamera()
        runOnUiThread {

            // Update local participant view
//            binding.mainParticipant.text = binding.participantName.text.toString()
//            binding.mainParticipant.setPadding(20, 3, 20, 3)
        }

        // Initialize and connect the websocket to OpenVidu Server
        startWebSocket()
    }

    private fun startWebSocket() {
        val webSocket = CustomWebSocket(session, this)
        webSocket.execute()
        session!!.setWebSocket(webSocket)
    }

    private fun connectionError(url: String?) {
        val myRunnable = Runnable {
            val toast = Toast.makeText(this, "Error connecting to $url", Toast.LENGTH_LONG)
            toast.show()
            viewToDisconnectedState()
        }
        Handler(this.mainLooper).post(myRunnable)
    }

    private fun initViews() {
        val rootEglBase = EglBase.create()
        binding.localGlSurfaceView.init(rootEglBase.eglBaseContext, null)
        binding.localGlSurfaceView.setMirror(true)
        binding.localGlSurfaceView.setEnableHardwareScaler(true)
        binding.localGlSurfaceView.setZOrderMediaOverlay(true)
    }

    fun viewToDisconnectedState() {
        runOnUiThread {
            binding.localGlSurfaceView.clearImage()
            binding.localGlSurfaceView.release()
//            binding.startFinishCall.text = resources.getString(R.string.start_button)
//            binding.startFinishCall.isEnabled = true
            binding.finishCall.isEnabled = true
//            binding.applicationServerUrl.isEnabled = true
//            binding.applicationServerUrl.isFocusableInTouchMode = true
//            binding.sessionName.isEnabled = true
//            binding.sessionName.isFocusableInTouchMode = true
//            binding.participantName.isEnabled = true
//            binding.participantName.isFocusableInTouchMode = true
            binding.mainParticipant.setText(null);
            binding.mainParticipant.setPadding(0, 0, 0, 0)
        }
    }

    fun viewToConnectingState() {
        runOnUiThread {
//            binding.startFinishCall.isEnabled = false
//            binding.applicationServerUrl.isEnabled = false
//            binding.applicationServerUrl.isFocusable = false
//            binding.sessionName.isEnabled = false
//            binding.sessionName.isFocusable = false
//            binding.participantName.isEnabled = false
//            binding.participantName.isFocusable = false
        }
    }

    fun viewToConnectedState() {
        runOnUiThread {
//            binding.startFinishCall.text = resources.getString(R.string.hang_up)
//            binding.startFinishCall.isEnabled = true
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
            binding.viewsContainer.addView(rowView)
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
            httpClient!!.dispose()
        }
        viewToDisconnectedState()
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