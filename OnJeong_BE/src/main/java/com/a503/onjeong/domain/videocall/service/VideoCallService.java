package com.a503.onjeong.domain.videocall.service;

import com.a503.onjeong.domain.videocall.dto.CallRequestDto;
import com.a503.onjeong.domain.videocall.dto.SessionIdRequestDto;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;

public interface VideoCallService {
    String initializeSession(SessionIdRequestDto sessionIdRequestDto) throws OpenViduJavaClientException, OpenViduHttpException;

    Connection createConnection(String sessionId) throws OpenViduJavaClientException, OpenViduHttpException;

    void sendAlert(CallRequestDto callRequestDto);
}
