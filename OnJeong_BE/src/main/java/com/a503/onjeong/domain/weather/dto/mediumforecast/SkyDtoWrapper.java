package com.a503.onjeong.domain.weather.dto.mediumforecast;

import lombok.Data;

import java.util.List;

@Data
public class SkyDtoWrapper {
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
        private List<SkyDto> item;
    }
}
