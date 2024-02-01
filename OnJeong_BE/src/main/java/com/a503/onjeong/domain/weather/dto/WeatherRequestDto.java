package com.a503.onjeong.domain.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherRequestDto {

    private String sido;
    private String gugun;
    private String dong;
}
