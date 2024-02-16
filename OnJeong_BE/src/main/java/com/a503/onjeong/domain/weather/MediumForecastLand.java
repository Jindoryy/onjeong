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
public class MediumForecastLand {

    @Id
    @Column(name = "sido")
    private String sido;

    @Column(name = "code")
    private String code;

    @Column(name = "base_time")
    private String base;

    @OneToMany(mappedBy = "mediumForecastLand")
    private List<SkyStatus> skyStatusList = new ArrayList<>();

    public void updateSkyStatusList(List<SkyStatus> skyStatusList){
        this.skyStatusList = skyStatusList;
    }

    public void updateBase(String base){
        this.base = base;
    }
}
