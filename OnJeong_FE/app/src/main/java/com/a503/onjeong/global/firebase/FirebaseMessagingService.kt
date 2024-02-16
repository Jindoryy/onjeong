package com.a503.onjeong.global.firebase


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.a503.onjeong.R
import com.a503.onjeong.domain.videocall.activity.RejectCallActivity
import com.a503.onjeong.domain.videocall.activity.VideoCallActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Random


class FirebaseMessagingService : FirebaseMessagingService() {
    private val NOTIFICATION_CHANNEL_ID = "notification.videocall"
    private val NOTIFICATION_CHANNEL_NAME = "VideoCall"
    private val NOTIFICATION_CHANNEL_DESCRIPTION = "notification channel"

    override fun onNewToken(token: String) {
        Log.d("FCM Log", "Refreshed token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM Log", "Notification Message Received: $remoteMessage")

        if (remoteMessage.data.isNotEmpty()) {
            val notificationId = 147147
            val sessionId = remoteMessage.data["sessionId"]
            val callerName = remoteMessage.data["callerName"]
            Log.d("FCM Log", "Notification Message content: $sessionId")

            val intent = Intent(this, VideoCallActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.putExtra("sessionId", sessionId)
            intent.putExtra("notificationId", notificationId)
            val pendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            val rejectIntent = Intent(this, RejectCallActivity::class.java)
            rejectIntent.putExtra("notificationId", notificationId)
            val rejectPendingIntent = PendingIntent.getActivity(
                this,
                1,
                rejectIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            showNotification(callerName, pendingIntent, rejectPendingIntent, notificationId)
        }
    }

    private fun showNotification(
        callerName: String?,
        pendingIntent: PendingIntent,
        rejectPendingIntent: PendingIntent,
        notificationId: Int
    ) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("${callerName}님으로부터 영상통화가 왔습니다!")
                .setContentText("\n\n\n")
                .setContentInfo("Info")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setCategory(NotificationCompat.CATEGORY_CALL)
//                .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
                .setFullScreenIntent(pendingIntent, true)
//                .setContentIntent(pendingIntent)
                .addAction(
                    NotificationCompat.Action.Builder(
                        android.R.drawable.sym_action_call,
                        "전화 받기",
                        pendingIntent
                    ).build()
                )
                .addAction(
                    NotificationCompat.Action.Builder(
                        android.R.drawable.stat_notify_missed_call,
                        "전화 거절",
                        rejectPendingIntent
                    ).build()
                )
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannel(manager: NotificationManager) {
        var notificationChannel: NotificationChannel? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_DESCRIPTION
            notificationChannel.setShowBadge(true)
            manager.createNotificationChannel(notificationChannel)
        }
    }
}