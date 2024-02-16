package com.a503.onjeong.domain.weather.service;

public enum WeatherStrategyName {

    SHORT_STRATEGY("weatherShortStrategy"), MEDIUM_TEMPERATURES_STRATEGY("weatherMediumTemperaturesStrategy"), MEDIUM_LAND_STRATEGY("weatherMediumLandStrategy");

    final String type;

    WeatherStrategyName(String type) {
        this.type = type;
    }
}
