package com.a503.onjeong.domain.weather.service;

import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class WeatherInfoCacheResponse {
    Boolean isEmpty;
    List<WeatherResponseDto> weatherResponseDtoList;
}
