package com.a503.onjeong.domain.videocall.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotiDto {
    private String token;
    private String content;

}