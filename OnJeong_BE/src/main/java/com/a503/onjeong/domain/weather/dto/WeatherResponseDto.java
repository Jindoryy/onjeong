package com.a503.onjeong.domain.weather.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WeatherResponseDto {

    // 몇번째 날짜
    private int days;

    // 현재 기온
    private double temperatures;

    // 최저 기온
    private double temperaturesLow;

    // 최고 기온
    private double temperaturesHigh;

    // 하늘 상태
    // 맑음(1), 구름많음(3), 흐림(4)
    private int sky;

    // 강수 형태
    // 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
    private int pty;

    public WeatherResponseDto(int days) {
        this.days = days;
    }

    public void setTemperatures(double temperatures, double temperaturesLow, double temperaturesHigh){
        this.temperatures = temperatures;
        this.temperaturesLow = temperaturesLow;
        this.temperaturesHigh = temperaturesHigh;
    }

    public void setSkyStatus(int sky, int pty){
        this.sky = sky;
        this.pty = pty;
    }

}
