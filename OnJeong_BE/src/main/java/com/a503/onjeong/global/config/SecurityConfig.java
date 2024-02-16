package com.a503.onjeong.global.config;

import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.global.auth.filter.JwtFilter;
import com.a503.onjeong.global.auth.filter.JwtReissueFilter;
import com.a503.onjeong.global.auth.filter.LoginFilter;
import com.a503.onjeong.global.auth.service.AuthService;
import com.a503.onjeong.global.auth.service.KakaoService;
import com.a503.onjeong.global.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


/* 시큐리티 설정 클래스 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthService authService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final KakaoService kakaoService;
    private final UserRepository userRepository;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 패스워드는 BCryptPasswordEncoder로 보안
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), userRepository, kakaoService, authenticationEntryPoint);
        JwtFilter checkFilter = new JwtFilter(authenticationManager(authenticationConfiguration), userRepository, authenticationEntryPoint, authService);
        JwtReissueFilter jwtReissueFilter = new JwtReissueFilter(authenticationManager(authenticationConfiguration), userRepository, authenticationEntryPoint);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class) // 로그인 필터 등록
                .addFilterBefore(checkFilter, BasicAuthenticationFilter.class) // 체크 필터 등록
                .addFilterBefore(jwtReissueFilter, UsernamePasswordAuthenticationFilter.class) // 토큰 재발급 필터 등록
                .exceptionHandling(authenticationManager -> authenticationManager
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())); // 예외 엔트리 포인트
        return http.build();
    }

    /* 스프링 시큐리티 룰을 무시하게 하는 Url 규칙(여기 등록하면 시큐리티 적용하지 않음) */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/doc", "/swagger*/**", "/favicon*/**", "/v2/api-docs") // 스웨거 괸련 경로 허용
                .requestMatchers("/auth/**"); // 로그인 관련 경로 허용
                // 검증이 필요 없는 경로를 추가해야함

    }

}