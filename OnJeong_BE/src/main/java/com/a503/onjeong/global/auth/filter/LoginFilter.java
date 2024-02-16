package com.a503.onjeong.global.auth.filter;

import com.a503.onjeong.domain.user.User;
import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.global.auth.dto.KakaoDto;
import com.a503.onjeong.global.auth.dto.LoginFilterRequestDto;
import com.a503.onjeong.global.auth.service.KakaoService;
import com.a503.onjeong.global.exception.ExceptionCodeSet;
import com.a503.onjeong.global.exception.FilterException;
import com.a503.onjeong.global.exception.UserException;
import com.a503.onjeong.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jdi.InternalException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

/* 로그인 시 검증 후 토큰 발급하는 필터 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private User user;
    private KakaoDto.UserInfo userInfo;
    private final UserRepository userRepository;
    private final KakaoService kakaoService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String BEARER = "Bearer ";

    public LoginFilter(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       KakaoService kakaoService,
                       AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.kakaoService = kakaoService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        LoginFilterRequestDto userDto = null;
        try {
            userDto = objectMapper.readValue(request.getInputStream(), LoginFilterRequestDto.class);
        } catch (IOException e) {
            unsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.INTERNAL_SERVER_ERROR));
        }

        userInfo = kakaoService.getUserInfo(userDto.getKakaoAccessToken(), userDto.getKakaoRefreshToken());
        User userOpt = userRepository.findByKakaoId(userInfo.getKakaoId()).orElseThrow(
                () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
        );

        // 검증 (카카오 액세스 토큰을 통해 추출된 유저의 id = 클라이언트에게 받은 유저의 id)
        if (Objects.equals(userOpt.getId(), userDto.getUserId())) {
            user = userOpt;
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    user.getId(), user.getId()
            );
            return getAuthenticationManager().authenticate(authenticationToken);
        }
        unsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.CREDENTIAL_FAIL));
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {

        // JWT 생성
        String accessToken = JwtUtil.createAccessToken(String.valueOf(user.getId()), LocalDateTime.now());
        String refreshToken = JwtUtil.createRefreshToken(String.valueOf(user.getId()), LocalDateTime.now());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        JSONObject json = new JSONObject();
        json.put("userId", user.getId());
        json.put("accessToken", BEARER + accessToken);
        json.put("refreshToken", BEARER + refreshToken);

        json.put("kakaoAccessToken", userInfo.getKakaoAccessToken());
        json.put("kakaoRefreshToken", userInfo.getKakaoRefreshToken());
        response.getWriter().write(String.valueOf(json));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) {
        SecurityContextHolder.clearContext();
        try {
            authenticationEntryPoint.commence(request, response, failed);
        } catch (ServletException | IOException e) {
            throw new FilterException(ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        }
    }
}