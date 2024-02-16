package com.a503.onjeong.domain.weather.service;

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherServiceImpl implements WeatherService {

    private final LockService lockService;

    @Transactional
    public List<WeatherResponseDto> getWeatherInfo(WeatherRequestDto requestDto) {

        List<WeatherResponseDto> weatherResponseDtoList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            weatherResponseDtoList.add(new WeatherResponseDto(i + 1));
        }

        weatherResponseDtoList = lockService.getWeatherInfoWithLock(WeatherStrategyName.SHORT_STRATEGY.type, requestDto, weatherResponseDtoList);
        weatherResponseDtoList = lockService.getWeatherInfoWithLock(WeatherStrategyName.MEDIUM_TEMPERATURES_STRATEGY.type, requestDto, weatherResponseDtoList);
        return lockService.getWeatherInfoWithLock(WeatherStrategyName.MEDIUM_LAND_STRATEGY.type, requestDto, weatherResponseDtoList);
    }


}
