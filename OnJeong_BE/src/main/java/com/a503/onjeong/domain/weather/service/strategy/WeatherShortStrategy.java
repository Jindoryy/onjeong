package com.a503.onjeong.domain.weather.service.strategy;


import com.a503.onjeong.domain.weather.ShortForecast;
import com.a503.onjeong.domain.weather.SkyStatus;
import com.a503.onjeong.domain.weather.Temperatures;
import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import com.a503.onjeong.domain.weather.dto.shortforecast.ShortForecastDto;
import com.a503.onjeong.domain.weather.dto.shortforecast.ShortForecastDtoWrapper;
import com.a503.onjeong.domain.weather.repository.ShortForecastRepository;
import com.a503.onjeong.domain.weather.repository.SkyStatusRepository;
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

@RequiredArgsConstructor
@Service
public class WeatherShortStrategy implements WeatherStrategy {

    private final ShortForecastRepository shortForecastRepository;
    private final TemperaturesRepository temperaturesRepository;
    private final SkyStatusRepository skyStatusRepository;

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

        /* 1일~3일 날씨 조회 */

        // 현재 위치의 x, y 좌표 구하기
        String sido1 = requestDto.getSido1();
        String sido2 = requestDto.getSido2();
        String gugun = requestDto.getGugun();
        String dong = requestDto.getDong();

        List<ShortForecast> shortForecastList = shortForecastRepository.findCodeBySidoAndGugunAndDong(sido1, sido2 + gugun, dong);
        if (shortForecastList.isEmpty()) {
            shortForecastList = shortForecastRepository.findCodeBySidoAndGugun(sido1, sido2 + gugun);
            if (shortForecastList.isEmpty()) {
                shortForecastList = shortForecastRepository.findCodeBySido(sido1);
                if (shortForecastList.isEmpty()) {
                    throw new WeatherException(ExceptionCodeSet.ADDRESS_NOT_FOUND);
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        String shortBase = getBaseTime(now);

        ShortForecast shortForecast = shortForecastList.get(0);

        List<Temperatures> shortTemperaturesList = shortForecast.getTemperaturesList();
        List<SkyStatus> shortSkyStatusList = shortForecast.getSkyStatusList();

        // 캐싱
        if (shortForecast.getBase() != null && shortForecast.getBase().equals(shortBase)) {
            for (int i = 0; i < 3; i++) {
                WeatherResponseDto weatherResponseDto = weatherResponseDtoList.get(i);
                Temperatures temperatures = shortTemperaturesList.get(i);
                SkyStatus skyStatus = shortSkyStatusList.get(i);

                weatherResponseDto
                        .setTemperatures(temperatures.getTemperaturesCurrent(),
                                temperatures.getTemperaturesLow(), temperatures.getTemperaturesHigh());
                weatherResponseDto.setSkyStatus(skyStatus.getSky(), skyStatus.getPty());
            }
            return new WeatherInfoCacheResponse(false, weatherResponseDtoList);
        }
        return new WeatherInfoCacheResponse(true, weatherResponseDtoList);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized List<WeatherResponseDto> getWeatherInfo(List<WeatherResponseDto> weatherResponseDtoList, WeatherRequestDto requestDto) {

        String[] days = new String[7];
        LocalDateTime today = LocalDateTime.now();
        String hours = String.valueOf(today.getHour());
        if (hours.length() == 1) hours = "0" + hours;

        days[0] = today.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + hours + "00";
        for (int i = 1; i < 3; i++) {
            today = today.plusDays(1);
            days[i] = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }

        String oneDay = days[0].substring(0, 8);
        String oneTime = days[0].substring(8);
        String twoDay = days[1];
        String threeDay = days[2];
        int flag;

        LocalDateTime now = LocalDateTime.now();
        String shortBase = getBaseTime(now);
        String baseDays = shortBase.substring(0, 8);
        String baseTime = shortBase.substring(8);

        // 현재 위치의 x, y 좌표 구하기
        String sido1 = requestDto.getSido1();
        String sido2 = requestDto.getSido2();
        String gugun = requestDto.getGugun();
        String dong = requestDto.getDong();

        List<ShortForecast> shortForecastList = shortForecastRepository.findCodeBySidoAndGugunAndDong(sido1, sido2 + gugun, dong);
        if (shortForecastList.isEmpty()) {
            shortForecastList = shortForecastRepository.findCodeBySidoAndGugun(sido1, sido2 + gugun);
            if (shortForecastList.isEmpty()) {
                shortForecastList = shortForecastRepository.findCodeBySido(sido1);
                if (shortForecastList.isEmpty()) {
                    throw new WeatherException(ExceptionCodeSet.ADDRESS_NOT_FOUND);
                }
            }
        }

        ShortForecast shortForecast = shortForecastList.get(0);

        List<Temperatures> shortTemperaturesList = shortForecast.getTemperaturesList();
        List<SkyStatus> shortSkyStatusList = shortForecast.getSkyStatusList();

        int coordinateX = shortForecast.getCoordinateX();
        int coordinateY = shortForecast.getCoordinateY();

        // API 요청
        MultiValueMap<String, String> shortWeatherParam = new LinkedMultiValueMap<>();
        shortWeatherParam.add("serviceKey", API_KEY);
        shortWeatherParam.add("pageNo", "1");
        shortWeatherParam.add("numOfRows", "800");
        shortWeatherParam.add("dataType", "JSON");
        shortWeatherParam.add("base_date", baseDays);
        shortWeatherParam.add("base_time", baseTime);
        shortWeatherParam.add("nx", String.valueOf(coordinateX));
        shortWeatherParam.add("ny", String.valueOf(coordinateY));

        ShortForecastDtoWrapper shortWrapper = webClient.get()
                .uri(uriBuilder ->
                        uriBuilder.scheme("http")
                                .host("apis.data.go.kr")
                                .path("/1360000/VilageFcstInfoService_2.0/getVilageFcst")
                                .queryParams(shortWeatherParam)
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept", "*/*;q=0.9") // HTTP_ERROR 방지)
                .retrieve()
                .bodyToMono(ShortForecastDtoWrapper.class)
                .block();

        List<ShortForecastDto> shortForecastDtoList = shortWrapper
                .getResponse()
                .getBody()
                .getItems()
                .getItem();

        for (ShortForecastDto data : shortForecastDtoList) {
            if (data.getFcstDate().equals(oneDay)) {
                // 1일 현재 기온
                if (data.getFcstTime().equals(oneTime) && data.getCategory().equals("TMP")) {
                    weatherResponseDtoList.get(0).setTemperatures(Double.valueOf(data.getFcstValue()));
                }

                // 1일 최고 기온
                if (data.getCategory().equals("TMX")) {
                    weatherResponseDtoList.get(0).setTemperaturesHigh(Double.valueOf(data.getFcstValue()));
                }

                // 1일 최저 기온
                if (data.getCategory().equals("TMN")) {
                    weatherResponseDtoList.get(0).setTemperaturesLow(Double.valueOf(data.getFcstValue()));
                }

                // 1일 하늘 상태
                if (data.getCategory().equals("SKY")) {
                    weatherResponseDtoList.get(0).setSky(Integer.valueOf(data.getFcstValue()));
                }

                // 1일 강수 형태
                if (data.getCategory().equals("PTY")) {
                    weatherResponseDtoList.get(0).setPty(Integer.valueOf(data.getFcstValue()));
                }
            }

            if (data.getFcstDate().equals(twoDay)) {
                // 2일 최고 기온
                if (data.getCategory().equals("TMX")) {
                    weatherResponseDtoList.get(1).setTemperaturesHigh(Double.valueOf(data.getFcstValue()));
                }

                // 2일 최저 기온
                if (data.getCategory().equals("TMN")) {
                    weatherResponseDtoList.get(1).setTemperaturesLow(Double.valueOf(data.getFcstValue()));
                }

                // 2일 하늘 상태
                if (data.getCategory().equals("SKY")) {
                    weatherResponseDtoList.get(1).setSky(Integer.valueOf(data.getFcstValue()));
                }

                // 2일 강수 형태
                if (data.getCategory().equals("PTY")) {
                    weatherResponseDtoList.get(1).setPty(Integer.valueOf(data.getFcstValue()));
                }
            }

            if (data.getFcstDate().equals(threeDay)) {
                // 3일 최고 기온
                if (data.getCategory().equals("TMX")) {
                    weatherResponseDtoList.get(2).setTemperaturesHigh(Double.valueOf(data.getFcstValue()));
                }

                // 3일 최저 기온
                if (data.getCategory().equals("TMN")) {
                    weatherResponseDtoList.get(2).setTemperaturesLow(Double.valueOf(data.getFcstValue()));
                }

                // 2일 하늘 상태
                if (data.getCategory().equals("SKY")) {
                    weatherResponseDtoList.get(2).setSky(Integer.valueOf(data.getFcstValue()));
                }

                // 3일 강수 형태
                if (data.getCategory().equals("PTY")) {
                    weatherResponseDtoList.get(2).setPty(Integer.valueOf(data.getFcstValue()));
                }
            }
        }

        flag = 0;
        if (shortTemperaturesList.isEmpty() || shortSkyStatusList.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                shortTemperaturesList.add(new Temperatures(shortForecast));
                shortSkyStatusList.add(new SkyStatus(shortForecast));
            }
            flag = 1;
        }

        for (int i = 0; i < 3; i++) {
            WeatherResponseDto weatherResponseDto = weatherResponseDtoList.get(i);
            Temperatures temperatures = shortTemperaturesList.get(i);
            SkyStatus skyStatus = shortSkyStatusList.get(i);

            temperatures.updateAllTemperatures(i + 1, weatherResponseDto.getTemperatures(),
                    weatherResponseDto.getTemperaturesLow(), weatherResponseDto.getTemperaturesHigh());

            skyStatus.updateSkyStatus(i + 1, skyStatus.getSky(), skyStatus.getPty());
        }

        // update
        shortForecast.updateTemperaturesList(shortTemperaturesList);
        shortForecast.updateSkyStatusList(shortSkyStatusList);
        shortForecast.updateBase(shortBase);

        if (flag == 1) {
            temperaturesRepository.saveAll(shortTemperaturesList);
            skyStatusRepository.saveAll(shortSkyStatusList);
        }

        return weatherResponseDtoList;
    }

    public String getBaseTime(LocalDateTime now) {
        int hours = now.getHour();
        if (hours < 2)
            return now.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "2300";
        return now.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "0200";
    }

}
