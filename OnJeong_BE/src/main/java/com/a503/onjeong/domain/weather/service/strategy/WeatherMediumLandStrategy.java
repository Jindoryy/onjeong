package com.a503.onjeong.domain.weather.service.strategy;


import com.a503.onjeong.domain.weather.MediumForecastLand;
import com.a503.onjeong.domain.weather.SkyStatus;
import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import com.a503.onjeong.domain.weather.dto.mediumforecast.SkyDto;
import com.a503.onjeong.domain.weather.dto.mediumforecast.SkyDtoWrapper;
import com.a503.onjeong.domain.weather.repository.MediumForecastLandRepository;
import com.a503.onjeong.domain.weather.repository.SkyStatusRepository;
import com.a503.onjeong.domain.weather.service.WeatherInfoCacheResponse;
import com.a503.onjeong.global.exception.ExceptionCodeSet;
import com.a503.onjeong.global.exception.WeatherException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
public class WeatherMediumLandStrategy implements WeatherStrategy {

    private final MediumForecastLandRepository mediumForecastLandRepository;
    private final SkyStatusRepository skyStatusRepository;

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

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public WeatherInfoCacheResponse getWeatherInfoByCaching(List<WeatherResponseDto> weatherResponseDtoList, WeatherRequestDto requestDto) {

        // 현재 위치의 x, y 좌표 구하기
        String sido1 = requestDto.getSido1();
        LocalDateTime now = LocalDateTime.now();

        // 2. 하늘/강수 상태/형태 API

        MediumForecastLand mediumForecastLand = mediumForecastLandRepository.findCodeBySido(sido1).orElseThrow(
                () -> new WeatherException(ExceptionCodeSet.ADDRESS_NOT_FOUND)
        );
        List<SkyStatus> mediumSkyStatusList = mediumForecastLand.getSkyStatusList();


        String mediumBase;
        if (now.getHour() < 6) {
            mediumBase = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1800";
        } else {
            mediumBase = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0600";
        }

        // 캐시
        if (mediumForecastLand.getBase() != null && mediumForecastLand.getBase().equals(mediumBase)) {
            for (int i = 3; i < 7; i++) {
                WeatherResponseDto weatherResponseDto = weatherResponseDtoList.get(i);
                SkyStatus skyStatus = mediumSkyStatusList.get(i - 3);

                weatherResponseDto.setSkyStatus(skyStatus.getSky(), skyStatus.getPty());
            }
            return new WeatherInfoCacheResponse(false, weatherResponseDtoList);
        }
        return new WeatherInfoCacheResponse(true, weatherResponseDtoList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<WeatherResponseDto> getWeatherInfo(List<WeatherResponseDto> weatherResponseDtoList, WeatherRequestDto requestDto) {

        // 현재 위치의 x, y 좌표 구하기
        String sido1 = requestDto.getSido1();
        LocalDateTime now = LocalDateTime.now();

        // 2. 하늘/강수 상태/형태 API
        MediumForecastLand mediumForecastLand = mediumForecastLandRepository.findCodeBySido(sido1).orElseThrow(
                () -> new WeatherException(ExceptionCodeSet.ADDRESS_NOT_FOUND)
        );
        List<SkyStatus> mediumSkyStatusList = mediumForecastLand.getSkyStatusList();


        String mediumBase;
        if (now.getHour() < 6) {
            mediumBase = now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "1800";
        } else {
            mediumBase = now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0600";
        }

        String code = mediumForecastLand.getCode();
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

        // API 요청
        SkyDtoWrapper skyDtoWrapper = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme("http")
                                .host("apis.data.go.kr")
                                .path("1360000/MidFcstInfoService/getMidLandFcst")
                                .queryParams(mediumWeatherParam)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept", "*/*;q=0.9") // HTTP_ERROR 방지)
                .retrieve()
                .bodyToMono(SkyDtoWrapper.class)
                .block();

        List<SkyDto> skyDtoList = skyDtoWrapper
                .getResponse()
                .getBody()
                .getItems()
                .getItem();

        flag = 0;
        if (mediumSkyStatusList.isEmpty()) {
            for (int i = 0; i < 4; i++) mediumSkyStatusList.add(new SkyStatus(mediumForecastLand));
            flag = 1;
        }

        for (SkyDto skyDto : skyDtoList) {

            // 4일차 하늘상태
            int[] value = changeSkyStatus(skyDto.getWf3Pm());
            weatherResponseDtoList.get(3).setSky(value[0]);
            weatherResponseDtoList.get(3).setPty(value[1]);
            mediumSkyStatusList.get(0).updateSkyStatus(4, value[0], value[1]);

            // 5일차 하늘상태
            value = changeSkyStatus(skyDto.getWf4Pm());
            weatherResponseDtoList.get(4).setSky(value[0]);
            weatherResponseDtoList.get(4).setPty(value[1]);
            mediumSkyStatusList.get(1).updateSkyStatus(5, value[0], value[1]);

            // 6일차 하늘상태
            value = changeSkyStatus(skyDto.getWf5Pm());
            weatherResponseDtoList.get(5).setSky(value[0]);
            weatherResponseDtoList.get(5).setPty(value[1]);
            mediumSkyStatusList.get(2).updateSkyStatus(6, value[0], value[1]);

            // 7일차 하늘상태
            value = changeSkyStatus(skyDto.getWf6Pm());
            weatherResponseDtoList.get(6).setSky(value[0]);
            weatherResponseDtoList.get(6).setPty(value[1]);
            mediumSkyStatusList.get(3).updateSkyStatus(7, value[0], value[1]);
            break;
        }

        mediumForecastLand.updateSkyStatusList(mediumSkyStatusList);
        mediumForecastLand.updateBase(mediumBase);

        if (flag == 1) {
            skyStatusRepository.saveAll(mediumSkyStatusList);
        }
        return weatherResponseDtoList;
    }

    public int[] changeSkyStatus(String status) {

        int[] value = new int[2];

        if (status.equals("맑음")) {
            value[0] = 1;
            value[1] = 0;
        } else if (status.equals("구름많음")) {
            value[0] = 3;
            value[1] = 0;
        } else if (status.equals("구름많고 비")) {
            value[0] = 3;
            value[1] = 1;
        } else if (status.equals("구름많고 눈")) {
            value[0] = 3;
            value[1] = 3;
        } else if (status.equals("구름많고 비/눈")) {
            value[0] = 3;
            value[1] = 2;
        } else if (status.equals("구름많고 소나기")) {
            value[0] = 3;
            value[1] = 4;
        } else if (status.equals("흐림")) {
            value[0] = 4;
            value[1] = 0;
        } else if (status.equals("흐리고 비")) {
            value[0] = 4;
            value[1] = 1;
        } else if (status.equals("흐리고 눈")) {
            value[0] = 4;
            value[1] = 3;
        } else if (status.equals("흐리고 비/눈")) {
            value[0] = 4;
            value[1] = 2;
        } else if (status.equals("흐리고 소나기")) {
            value[0] = 4;
            value[1] = 4;
        }
        return value;
    }
}
