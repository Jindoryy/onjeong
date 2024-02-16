package com.a503.onjeong.domain.weather.repository;

import com.a503.onjeong.domain.weather.MediumForecastTemperatures;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MediumForecastTemperaturesRepository extends JpaRepository<MediumForecastTemperatures, String> {

    // 시도1으로 code 찾기 (광역시)
    @Query("select f from MediumForecastTemperatures f where f.sido = :sido1")
    MediumForecastTemperatures findCodeBySidoOne(String sido1);

    // 시도2로 code 찾기
    @Query("select f from MediumForecastTemperatures f where f.sido = :sido2")
    MediumForecastTemperatures findCodeBySidoTwo(String sido2);

    // 구군으로 code 찾기
    @Query("select f from MediumForecastTemperatures f where f.sido = :gugun")
    MediumForecastTemperatures findCodeByGugun(String gugun);

}
