package com.a503.onjeong.domain.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherRequestDto {

    private String sido1;
    private String sido2;
    private String gugun;
    private String dong;
}
