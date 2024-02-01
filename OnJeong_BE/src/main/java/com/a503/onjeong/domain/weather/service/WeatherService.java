package com.a503.onjeong.domain.weather.service;

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;

import java.util.List;

public interface WeatherService {

    List<WeatherResponseDto> getWeatherInfo(WeatherRequestDto requestDto);
}
