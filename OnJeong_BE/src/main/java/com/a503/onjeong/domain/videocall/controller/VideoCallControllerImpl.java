package com.a503.onjeong.domain.videocall.controller;

import com.a503.onjeong.domain.videocall.dto.CallRequestDto;
import com.a503.onjeong.domain.videocall.dto.SessionIdRequestDto;
import com.a503.onjeong.domain.videocall.service.VideoCallService;
import io.openvidu.java.client.Connection;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video-call")
@RequiredArgsConstructor
public class VideoCallControllerImpl implements VideoCallController {

    private final VideoCallService videoCallService;

    @Override
    @PostMapping("/sessions")
    public ResponseEntity<String> initializeSession(@RequestBody SessionIdRequestDto sessionIdRequestDto)
            throws OpenViduJavaClientException, OpenViduHttpException {
        String sessionId = videoCallService.initializeSession(sessionIdRequestDto);
        return new ResponseEntity<>(sessionId, HttpStatus.OK);
    }

    @Override
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId) throws OpenViduJavaClientException, OpenViduHttpException {

        Connection connection = videoCallService.createConnection(sessionId);
        if (connection == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

    @Override
    @PostMapping("/alert")
    public ResponseEntity<Void> sendAlert(@RequestBody CallRequestDto callRequestDto) {
        videoCallService.sendAlert(callRequestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
