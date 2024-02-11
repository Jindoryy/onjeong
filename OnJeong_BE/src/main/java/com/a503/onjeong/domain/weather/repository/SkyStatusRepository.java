package com.a503.onjeong.domain.weather.repository;

import com.a503.onjeong.domain.weather.SkyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkyStatusRepository extends JpaRepository<SkyStatus, Long> {
}
