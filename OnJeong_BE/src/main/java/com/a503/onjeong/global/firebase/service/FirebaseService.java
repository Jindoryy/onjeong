package com.a503.onjeong.global.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    public String sendVideoCallNotification(String fcmToken, String sessionId, String callerName) {
        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .putData("sessionId", sessionId)
                    .putData("callerName", callerName)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            // return response if firebase messaging is successfully completed.
            return response;

        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            return "Failed";
        }
    }
}