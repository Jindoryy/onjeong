package com.a503.onjeong.domain.videocall.service;

import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.domain.videocall.dto.CallRequestDto;
import com.a503.onjeong.domain.videocall.dto.SessionIdRequestDto;
import com.a503.onjeong.global.firebase.service.FirebaseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VideoCallServiceImpl implements VideoCallService {
    @Value("${openvidu.url}")
    private String OPENVIDU_URL;

    @Value("${openvidu.secret}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;

    private final UserRepository userRepository;

    private final FirebaseService firebaseService;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    @Override
    public String initializeSession(SessionIdRequestDto sessionIdRequestDto) throws OpenViduJavaClientException, OpenViduHttpException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(sessionIdRequestDto, Map.class);
        SessionProperties properties = SessionProperties.fromJson(param).build();
        Session session = openvidu.createSession(properties);
        return session.getSessionId();
    }

    @Override
    public Connection createConnection(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {
        Session session = openvidu.getActiveSession(sessionId);
        if (session == null) {
            return null;
        }
        ConnectionProperties properties = ConnectionProperties.fromJson(null).build();
        return session.createConnection(properties);
    }

    @Override
    public void sendAlert(CallRequestDto callRequestDto) {
        ArrayList<Long> userIdList = callRequestDto.getUserIdList();
        String sessionId = callRequestDto.getSessionId();
        String callerName = callRequestDto.getCallerName();

        userRepository.findAllById(userIdList).forEach(user -> firebaseService.sendVideoCallNotification(user.getFcmToken(), sessionId, callerName));
    }
}
