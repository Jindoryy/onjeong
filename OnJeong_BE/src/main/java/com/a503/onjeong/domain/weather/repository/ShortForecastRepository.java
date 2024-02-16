package com.a503.onjeong.domain.weather.repository;

import com.a503.onjeong.domain.weather.ShortForecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ShortForecastRepository extends JpaRepository<ShortForecast, String> {

    @Query("select f from ShortForecast f " +
            "where f.sido = :sido1 and " +
            "f.gugun = :sido2Gugun and " +
            "f.dong = :dong" )
    List<ShortForecast> findCodeBySidoAndGugunAndDong(String sido1, String sido2Gugun, String dong);

    @Query("select f from ShortForecast f " +
            "where f.sido = :sido1 and " +
            "f.gugun = :sido2Gugun")
    List<ShortForecast> findCodeBySidoAndGugun(String sido1, String sido2Gugun);

    @Query("select f from ShortForecast f " +
            "where f.sido = :sido1")
    List<ShortForecast> findCodeBySido(String sido1);

}
