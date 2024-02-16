package com.a503.onjeong.domain.weather;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SkyStatus {
    @Id
    @Column(name = "sky_status_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="days")
    private int days;

    // 하늘 상태
    // 맑음(1), 구름많음(3), 흐림(4)
    @Column(name = "sky")
    private int sky;

    // 강수 형태
    // 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
    @Column(name = "pty")
    private int pty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code")
    private ShortForecast shortForecast;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sido")
    private MediumForecastLand mediumForecastLand;

    public SkyStatus(ShortForecast shortForecast){
        this.shortForecast = shortForecast;
    }

    public SkyStatus(MediumForecastLand mediumForecastLand){
        this.mediumForecastLand = mediumForecastLand;
    }

    public void updateSkyStatus(int days, int sky, int pty){
        this.days = days;
        this.sky = sky;
        this.pty = pty;
    }
}
