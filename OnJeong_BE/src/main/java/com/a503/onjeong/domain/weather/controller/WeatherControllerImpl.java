package com.a503.onjeong.domain.weather.controller;

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import com.a503.onjeong.domain.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherControllerImpl implements WeatherController {

    private final WeatherService weatherService;

    @PostMapping("/info")
    public ResponseEntity<List<WeatherResponseDto>> getWeather(@RequestBody WeatherRequestDto requestDto) {
        return ResponseEntity.ok().body(weatherService.getWeatherInfo(requestDto));

    }
}
