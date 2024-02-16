package com.a503.onjeong.domain.weather.service.strategy;

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import com.a503.onjeong.domain.weather.service.WeatherInfoCacheResponse;

import java.util.List;

public interface WeatherStrategy {

    WeatherInfoCacheResponse getWeatherInfoByCaching(List<WeatherResponseDto> weatherResponseDtoList, WeatherRequestDto weatherRequestDto);

    List<WeatherResponseDto> getWeatherInfo(List<WeatherResponseDto> weatherResponseDtoList, WeatherRequestDto weatherRequestDto);

}
