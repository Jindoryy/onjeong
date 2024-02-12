package com.a503.onjeong.domain.videocall.dto;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;

@Data
@Getter
public class CallRequestDto {
    ArrayList<Long> userIdList;
    String sessionId;
    String callerName;
}
