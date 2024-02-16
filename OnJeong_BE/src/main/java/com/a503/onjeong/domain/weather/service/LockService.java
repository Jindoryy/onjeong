package com.a503.onjeong.domain.weather.service;

import com.a503.onjeong.domain.weather.dto.WeatherRequestDto;
import com.a503.onjeong.domain.weather.dto.WeatherResponseDto;
import com.a503.onjeong.domain.weather.service.strategy.WeatherStrategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LockService {

    private final Map<String, WeatherStrategy> strategyList;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    protected Long getLock(String lockName, int timeout) {
        Query query = entityManager.createNativeQuery("SELECT GET_LOCK(:lockName, :timeout)")
                .setParameter("lockName", lockName)
                .setParameter("timeout", timeout);
        return (Long) query.getSingleResult();
    }

    @Transactional
    protected void releaseLock(String lockName) {
        Query query = entityManager.createNativeQuery("DO RELEASE_LOCK(:lockName)")
                .setParameter("lockName", lockName);
        query.executeUpdate();
    }

    @Transactional
    public List<WeatherResponseDto> getWeatherInfoWithLock(String strategyName, WeatherRequestDto requestDto, List<WeatherResponseDto> weatherResponseDtoList) {

        WeatherStrategy weatherStrategy = strategyList.get(strategyName);

        String address = requestDto.getSido1() + requestDto.getSido2() + requestDto.getGugun() + requestDto.getDong();
        WeatherInfoCacheResponse weatherInfoCacheResponse;

        // 락 걸기
        try {
            getLock(address, 3000);
            weatherInfoCacheResponse = weatherStrategy.getWeatherInfoByCaching(weatherResponseDtoList, requestDto);
            if (weatherInfoCacheResponse.isEmpty) return weatherStrategy.getWeatherInfo(weatherResponseDtoList, requestDto);
        } finally {
            releaseLock(address);
        }
        return weatherInfoCacheResponse.weatherResponseDtoList;
    }
}
