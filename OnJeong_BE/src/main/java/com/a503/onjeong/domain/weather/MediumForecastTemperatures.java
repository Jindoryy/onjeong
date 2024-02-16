package com.a503.onjeong.domain.weather;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediumForecastTemperatures {

    @Id
    @Column(name = "medium_code")
    private String code;

    @Column(name = "sido")
    private String sido;

    @Column(name = "base_time")
    private String base;

    @OneToMany(mappedBy = "mediumForecastTemperatures")
    private List<Temperatures> temperaturesList = new ArrayList<>();

    public void updateTemperaturesList(List<Temperatures> temperaturesList){
        this.temperaturesList = temperaturesList;
    }

    public void updateBase(String base){
        this.base = base;
    }
}
