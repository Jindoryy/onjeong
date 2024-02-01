package com.a503.onjeong.domain.weather.dto.shortforecast;

import lombok.Data;

@Data
public class ShortForecastDto {
    private String baseDate;
    private String baseTime;
    private String category;
    private String fcstDate;
    private String fcstTime;
    private String fcstValue;
    private String nx;
    private String ny;
}
