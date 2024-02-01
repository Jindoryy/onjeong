package com.a503.onjeong.domain.weather.dto.shortforecast;

import lombok.Data;

import java.util.List;

@Data
public class ShortForecastDtoWrapper {

    private Response response;

    @Data
    public static class Response {
        private Body body;
    }

    @Data
    public static class Body {
        private Items items;
    }

    @Data
    public static class Items {
        private List<ShortForecastDto> item;
    }
}
