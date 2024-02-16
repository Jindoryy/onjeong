package com.a503.onjeong.domain.weather.controller;

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/weather")
@RestController
public interface WeatherController {

    @PostMapping("/info")
    ResponseEntity<List<WeatherResponseDto>> getWeather(@RequestBody WeatherRequestDto requestDto);
}
