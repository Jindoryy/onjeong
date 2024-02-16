package com.a503.onjeong.domain.weather;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Temperatures {

    @Id
    @Column(name = "temperatures_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "days")
    private int days;

    // 현재 기온
    @Column(name = "temperatures_current")
    private double temperaturesCurrent;

    // 최저 기온
    @Column(name = "temperatures_low")
    private double temperaturesLow;

    // 최고 기온
    @Column(name = "temperatures_high")
    private double temperaturesHigh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    private ShortForecast shortForecast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medium_code")
    private MediumForecastTemperatures mediumForecastTemperatures;

    public Temperatures(ShortForecast shortForecast){
        this.shortForecast = shortForecast;
    }

    public Temperatures(MediumForecastTemperatures mediumForecastTemperatures){
        this.mediumForecastTemperatures = mediumForecastTemperatures;
    }

    public void updateAllTemperatures(int days, double temperatures, double temperaturesLow, double temperaturesHigh){
        this.days = days;
        this.temperaturesCurrent = temperatures;
        this.temperaturesLow = temperaturesLow;
        this.temperaturesHigh = temperaturesHigh;
    }

    public void updateLowHighTemperatures(int days, double temperaturesLow, double temperaturesHigh){
        this.days = days;
        this.temperaturesLow = temperaturesLow;
        this.temperaturesHigh = temperaturesHigh;
    }

}
