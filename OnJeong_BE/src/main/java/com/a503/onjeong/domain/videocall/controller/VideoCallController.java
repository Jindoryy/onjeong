package com.a503.onjeong.domain.videocall.controller;

import com.a503.onjeong.domain.videocall.dto.CallRequestDto;
import com.a503.onjeong.domain.videocall.dto.SessionIdRequestDto;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface VideoCallController {
    @PostMapping("/sessions")
    ResponseEntity<String> initializeSession(@RequestBody SessionIdRequestDto sessionIdRequestDto)
            throws OpenViduJavaClientException, OpenViduHttpException;

    @GetMapping("sessions/{sessionId}")
    ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId) throws OpenViduJavaClientException, OpenViduHttpException;

    @PostMapping("/alert")
    ResponseEntity<Void> sendAlert(@RequestBody CallRequestDto callRequestDto);
}
