package com.a503.onjeong.domain.weather.repository;

import com.a503.onjeong.domain.weather.MediumForecastLand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MediumForecastLandRepository extends JpaRepository<MediumForecastLand, String> {

    // 시도로 code 찾기 (광역시)
    @Query("select f.code from MediumForecastLand f where f.sido like %:sido%")
    String findCodeBySido(String sido);

}
