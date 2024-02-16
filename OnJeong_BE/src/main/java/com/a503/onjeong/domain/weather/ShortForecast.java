package com.a503.onjeong.domain.weather;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortForecast {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "sido")
    private String sido;

    @Column(name = "gugun")
    private String gugun;

    @Column(name = "dong")
    private String dong;

    @Column(name = "coordinate_x")
    private int coordinateX;

    @Column(name = "coordinate_y")
    private int coordinateY;

    @Column(name = "base_time")
    private String base;

    @OneToMany(mappedBy = "shortForecast")
    private List<Temperatures> temperaturesList = new ArrayList<>();

    @OneToMany(mappedBy = "shortForecast")
    List<SkyStatus> skyStatusList = new ArrayList<>();

    public void updateTemperaturesList(List<Temperatures> temperaturesList){
        this.temperaturesList = temperaturesList;
    }

    public void updateSkyStatusList(List<SkyStatus> skyStatusList){
        this.skyStatusList = skyStatusList;
    }

    public void updateBase(String base){
        this.base = base;
    }
}
