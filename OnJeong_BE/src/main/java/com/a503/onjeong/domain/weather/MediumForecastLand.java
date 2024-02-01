package com.a503.onjeong.domain.weather;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MediumForecastLand {

    @Id
    @Column(name = "sido")
    private String sido;

    @Column(name = "code")
    private String code;
}
