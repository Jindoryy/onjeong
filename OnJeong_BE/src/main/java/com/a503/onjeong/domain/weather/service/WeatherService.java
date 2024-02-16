package com.a503.onjeong.domain.weather.service;

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WeatherService {

    List<WeatherResponseDto> getWeatherInfo(WeatherRequestDto requestDto);
}
