package com.a503.onjeong.domain.weather.service.strategy;


import com.a503.onjeong.domain.weather.MediumForecastTemperatures;
import com.a503.onjeong.domain.weather.Temperatures;
import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import com.a503.onjeong.domain.weather.dto.mediumforecast.TemperaturesDto;
import com.a503.onjeong.domain.weather.dto.mediumforecast.TemperaturesDtoWrapper;
import com.a503.onjeong.domain.weather.repository.MediumForecastTemperaturesRepository;
import com.a503.onjeong.domain.weather.repository.TemperaturesRepository;
import com.a503.onjeong.domain.weather.service.WeatherInfoCacheResponse;
import com.a503.onjeong.global.exception.ExceptionCodeSet;
import com.a503.onjeong.global.exception.WeatherException;
import com.sun.jdi.InternalException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherMediumTemperaturesStrategy implements WeatherStrategy {

    private final MediumForecastTemperaturesRepository mediumForecastTemperaturesRepository;
    private final TemperaturesRepository temperaturesRepository;

    private int flag = 0;

    @Value("${weather.API_KEY}")
    private String API_KEY;

    WebClient webClient;

    @PostConstruct
    public void initWebClient() {

        // uri 인코딩 x
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        webClient = WebClient.builder().uriBuilderFactory(factory).build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public WeatherInfoCacheResponse getWeatherInfoByCaching(List<WeatherResponseDto> weatherResponseDtoList, WeatherRequestDto requestDto) {

        // 현재 위치의 x, y 좌표 구하기
        String sido1 = requestDto.getSido1();
        String sido2 = requestDto.getSido2();
        String gugun = requestDto.getGugun();
        LocalDateTime now = LocalDateTime.now();

        /* 3일~7일 날씨 조회 */

        // 1. 최저, 최고 기온 API
        MediumForecastTemperatures mediumForecastTemperatures = mediumForecastTemperaturesRepository.findCodeBySidoOne(sido1);
        if (mediumForecastTemperatures == null) {
            mediumForecastTemperatures = mediumForecastTemperaturesRepository.findCodeBySidoTwo(sido2);
            if (mediumForecastTemperatures == null) {
                mediumForecastTemperatures = mediumForecastTemperaturesRepository.findCodeByGugun(gugun);
                if (mediumForecastTemperatures == null) {
                    throw new WeatherException(ExceptionCodeSet.ADDRESS_NOT_FOUND);
                }
            }
        }

        List<Temperatures> mediumTemperaturesList = mediumForecastTemperatures.getTemperaturesList();
        String mediumBase;
        if (now.getHour() < 6) {
            mediumBase = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1800";
        } else {
            mediumBase = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0600";
        }

        // 캐싱
        if (mediumForecastTemperatures.getBase() != null && mediumForecastTemperatures.getBase().equals(mediumBase)) {
            for (int i = 3; i < 7; i++) {
                WeatherResponseDto weatherResponseDto = weatherResponseDtoList.get(i);
                Temperatures temperatures = mediumTemperaturesList.get(i - 3);

                weatherResponseDto.setTemperatures(temperatures.getTemperaturesCurrent(),
                        temperatures.getTemperaturesLow(), temperatures.getTemperaturesHigh());
            }
            return new WeatherInfoCacheResponse(false, weatherResponseDtoList);
        }
        return new WeatherInfoCacheResponse(true, weatherResponseDtoList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<WeatherResponseDto> getWeatherInfo(List<WeatherResponseDto> weatherResponseDtoList, WeatherRequestDto requestDto) {

        // 현재 위치의 x, y 좌표 구하기
        String sido1 = requestDto.getSido1();
        String sido2 = requestDto.getSido2();
        String gugun = requestDto.getGugun();
        LocalDateTime now = LocalDateTime.now();

        /* 3일~7일 날씨 조회 */

        // 1. 최저, 최고 기온 API
        MediumForecastTemperatures mediumForecastTemperatures = mediumForecastTemperaturesRepository.findCodeBySidoOne(sido1);
        if (mediumForecastTemperatures == null) {
            mediumForecastTemperatures = mediumForecastTemperaturesRepository.findCodeBySidoTwo(sido2);
            if (mediumForecastTemperatures == null) {
                mediumForecastTemperatures = mediumForecastTemperaturesRepository.findCodeByGugun(gugun);
                if (mediumForecastTemperatures == null) {
                    throw new WeatherException(ExceptionCodeSet.ADDRESS_NOT_FOUND);
                }
            }
        }

        String code = mediumForecastTemperatures.getCode();

        List<Temperatures> mediumTemperaturesList = mediumForecastTemperatures.getTemperaturesList();
        String mediumBase;
        if (now.getHour() < 6) {
            mediumBase = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1800";
        } else {
            mediumBase = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0600";
        }

        MultiValueMap<String, String> mediumWeatherParam = new LinkedMultiValueMap<>();
        mediumWeatherParam.add("serviceKey", API_KEY);
        mediumWeatherParam.add("pageNo", "1");
        mediumWeatherParam.add("numOfRows", "10");
        mediumWeatherParam.add("dataType", "JSON");
        mediumWeatherParam.add("regId", code);
        if (now.getHour() < 6) {
            mediumWeatherParam.add("tmFc", mediumBase);
        } else {
            mediumWeatherParam.add("tmFc", mediumBase);
        }

        TemperaturesDtoWrapper temperaturesDtoWrapper = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme("http")
                                .host("apis.data.go.kr")
                                .path("1360000/MidFcstInfoService/getMidTa")
                                .queryParams(mediumWeatherParam)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept", "*/*;q=0.9") // HTTP_ERROR 방지)
                .retrieve()
                .bodyToMono(TemperaturesDtoWrapper.class)
                .block();

        List<TemperaturesDto> temperaturesDtoList = temperaturesDtoWrapper
                .getResponse()
                .getBody()
                .getItems()
                .getItem();

        flag = 0;
        if (mediumTemperaturesList.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                mediumTemperaturesList.add(new Temperatures(mediumForecastTemperatures));
            }
            flag = 1;
        }

        for (TemperaturesDto mediumForecastDto : temperaturesDtoList) {

            // 4일 최고, 최저 기온
            weatherResponseDtoList.get(3).setTemperaturesHigh(Double.parseDouble(mediumForecastDto.getTaMax3()));
            weatherResponseDtoList.get(3).setTemperaturesLow(Double.parseDouble(mediumForecastDto.getTaMin3()));
            mediumTemperaturesList.get(0).updateLowHighTemperatures(4, Double.parseDouble(mediumForecastDto.getTaMin3()), Double.parseDouble(mediumForecastDto.getTaMax3()));

            // 5일 최고, 최저 기온
            weatherResponseDtoList.get(4).setTemperaturesHigh(Double.parseDouble(mediumForecastDto.getTaMax4()));
            weatherResponseDtoList.get(4).setTemperaturesLow(Double.parseDouble(mediumForecastDto.getTaMin4()));
            mediumTemperaturesList.get(1).updateLowHighTemperatures(5, Double.parseDouble(mediumForecastDto.getTaMin4()), Double.parseDouble(mediumForecastDto.getTaMax4()));


            // 6일 최고, 최저 기온
            weatherResponseDtoList.get(5).setTemperaturesHigh(Double.parseDouble(mediumForecastDto.getTaMax5()));
            weatherResponseDtoList.get(5).setTemperaturesLow(Double.parseDouble(mediumForecastDto.getTaMin5()));
            mediumTemperaturesList.get(2).updateLowHighTemperatures(6, Double.parseDouble(mediumForecastDto.getTaMin5()), Double.parseDouble(mediumForecastDto.getTaMax5()));

            // 7일 최고, 최저 기온
            weatherResponseDtoList.get(6).setTemperaturesHigh(Double.parseDouble(mediumForecastDto.getTaMax6()));
            weatherResponseDtoList.get(6).setTemperaturesLow(Double.parseDouble(mediumForecastDto.getTaMin6()));
            mediumTemperaturesList.get(3).updateLowHighTemperatures(7, Double.parseDouble(mediumForecastDto.getTaMin6()), Double.parseDouble(mediumForecastDto.getTaMax6()));
        }

        mediumForecastTemperatures.updateTemperaturesList(mediumTemperaturesList);
        mediumForecastTemperatures.updateBase(mediumBase);

        if (flag == 1) temperaturesRepository.saveAll(mediumTemperaturesList);

        return weatherResponseDtoList;
    }
}
