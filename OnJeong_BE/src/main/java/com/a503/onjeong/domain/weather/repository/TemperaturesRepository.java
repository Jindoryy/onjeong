package com.a503.onjeong.domain.weather.repository;

import com.a503.onjeong.domain.weather.Temperatures;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemperaturesRepository extends JpaRepository<Temperatures, Long> {
}
