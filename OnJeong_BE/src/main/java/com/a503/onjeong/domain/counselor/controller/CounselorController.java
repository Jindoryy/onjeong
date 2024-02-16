package com.a503.onjeong.domain.counselor.controller;

import com.a503.onjeong.domain.counselor.dto.SessionIdRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.openvidu.java.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;

//방 정보를 담은 클래스
class Room{
    String id;
    boolean meet;
    Long user_id;
    public Room(String id, boolean meet, Long user_id){
        this.id=id;
        this.meet=meet;
        this.user_id=user_id;
    }
}

@RestController
@RequestMapping("/counselor")
public class CounselorController {
    @Value("${openvidu.url}")
    private String OPENVIDU_URL;

    @Value("${openvidu.secret}")
    private String OPENVIDU_SECRET;

    private OpenVidu openvidu;
    private Long userId;

    @PostConstruct
    public void init() {
        this.openvidu = new OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET);
    }

    /**
     * @param params The Session properties
     * @return The Session ID
     */
    static ArrayList<Room> emptySession = new ArrayList<>();

    //상담원 방 생성
    @PostMapping("/sessions")
    public ResponseEntity<String> initializeSession(@RequestBody(required = false)SessionIdRequestDto dto)
            throws OpenViduJavaClientException, OpenViduHttpException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> param = mapper.convertValue(dto, Map.class);
        SessionProperties properties = SessionProperties.fromJson(param).build();
        userId = dto.getUserId();

        Session session = openvidu.createSession(properties);

        return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
    }

    //상담원 방 접속
    @PostMapping("/sessions/{sessionId}/connections")
    public ResponseEntity<String> createConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) SessionIdRequestDto dto)
            throws OpenViduJavaClientException, OpenViduHttpException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = mapper.convertValue(dto, Map.class);

        Session session = openvidu.getActiveSession(sessionId);

        if (session == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        boolean found = false;
        Room existingRoom = null;
        for (Room room : emptySession) {
            if (room.id.equals(sessionId)) {
                existingRoom = room;
                found = true;
                break;
            }
        }
        if (found) {
            emptySession.remove(existingRoom);
        }
        emptySession.add(new Room(sessionId, false, userId));

        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

    //상담원 생성된 방 연결
    @PostMapping("/user/{sessionId}/connections")
    public ResponseEntity<String> userConnection(@PathVariable("sessionId") String sessionId,
                                                   @RequestBody(required = false) SessionIdRequestDto dto)
            throws OpenViduJavaClientException, OpenViduHttpException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params = mapper.convertValue(dto, Map.class);

        Session session = openvidu.getActiveSession(sessionId);
        ConnectionProperties properties = ConnectionProperties.fromJson(params).build();
        Connection connection = session.createConnection(properties);
        return new ResponseEntity<>(connection.getToken(), HttpStatus.OK);
    }

    //상담원 아이디 가져오기
    @GetMapping("/get/{sessionId}")
    public ResponseEntity<String> getUserId(@PathVariable String sessionId){
        Room roomForUser = null;
            for(Room room : emptySession){
                if(room.id.equals(sessionId)){
                    roomForUser = room;
                    break;
                }
            }
        Long result = roomForUser.user_id;
        return new ResponseEntity<>(String.valueOf(result), HttpStatus.OK);
    }

    //상담원 방 나가기
    @DeleteMapping("/delete/{sessionId}")
    public ResponseEntity<String> deleteRoom(@PathVariable String sessionId){
        Room roomToDelete = null;
        for(Room room : emptySession){
            if(room.id.equals(sessionId)){
                roomToDelete = room;
                break;
            }
        }

        if(roomToDelete != null){
            emptySession.remove(roomToDelete);
            return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Room not found", HttpStatus.NOT_FOUND);
        }
    }

    //클라이언트 상담 방 접속
    @PostMapping("/connect")
    public ResponseEntity<String> connectToEmptyRoom() {
        // 빈 방 정보를 확인하고 연결할 수 있는 빈 방을 찾음
        Room emptyRoom = findEmptyRoom();
        if (emptyRoom == null) {
            // 빈 방이 없는 경우 에러 응답 반환
            return new ResponseEntity<>("No empty room available", HttpStatus.NOT_FOUND);
        }
        // 빈 방을 연결 상태로 변경
        emptyRoom.meet = true;
        // 연결할 빈 방의 sessionId를 가져와서 해당 세션에 연결 요청을 생성
        try {
            Session session = openvidu.getActiveSession(emptyRoom.id);
            Connection connection = session.createConnection(new ConnectionProperties.Builder().build());
            return new ResponseEntity<>(session.getSessionId(), HttpStatus.OK);
        } catch (OpenViduJavaClientException | OpenViduHttpException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to create connection", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 빈 방 중 첫 번째로 발견되는 빈 방을 찾는 메서드
    private Room findEmptyRoom() {
        for (Room room : emptySession) {
            if (!room.meet) {
                return room;
            }
        }
        return null;
    }
    //클라이언트 방 나가기
    @PostMapping("/disconnect/{sessionId}")
    public ResponseEntity<String> disconnectFromRoom(@PathVariable("sessionId") String sessionId) {
        // 해당 sessionId에 대한 방을 찾아서 빈 상태로 변경
        for (Room room : emptySession) {
            if (room.id.equals(sessionId)) {
                room.meet = false;
                return new ResponseEntity<>("Disconnected from room: " + sessionId, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Room not found: " + sessionId, HttpStatus.NOT_FOUND);
    }
}
