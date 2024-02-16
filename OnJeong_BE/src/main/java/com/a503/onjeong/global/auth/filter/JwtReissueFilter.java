package com.a503.onjeong.global.auth.filter;

import com.a503.onjeong.domain.user.User;
import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.global.exception.ExceptionCodeSet;
import com.a503.onjeong.global.exception.FilterException;
import com.a503.onjeong.global.exception.UserException;
import com.a503.onjeong.global.util.JwtUtil;
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
import java.rmi.RemoteException;
import java.time.LocalDateTime;

/* JWT 재발급 필터 */
public class JwtReissueFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final UserRepository userRepository;

    private static final String BEARER = "Bearer ";

    private static final String REFRESH_TOKEN = "Refresh-Token";

    public JwtReissueFilter(AuthenticationManager authenticationManager,
                            UserRepository userRepository,
                            AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
        setFilterProcessesUrl("/reissue");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) {
        String refreshToken = request.getHeader(REFRESH_TOKEN).substring(BEARER.length());
        User user = null;
        Long userId = null;

        // 받은 리프레시 토큰 유효성 검사
        try {
            if (JwtUtil.verify(refreshToken)) {
                userId = Long.valueOf(JwtUtil.getSubject(refreshToken));
                if (!userRepository.existsById(Long.valueOf(userId)))
                    unsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.REFRESH_TOKEN_INVALID));
                user = userRepository.findById(userId).orElseThrow(
                        () -> new UserException(ExceptionCodeSet.USER_NOT_FOUND)
                );
            }
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.REFRESH_TOKEN_EXPIRED));
        }

        String refreshTokenDb = user.getRefreshToken().substring(BEARER.length());
        Long userIdDb = null;

        // db에 있는 리프레시 토큰 유효성 검사
        try {
            if (JwtUtil.verify(refreshTokenDb)) {
                userIdDb = Long.valueOf(JwtUtil.getSubject(refreshToken));
                if (!userRepository.existsById(Long.valueOf(userIdDb)))
                    unsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.REFRESH_TOKEN_INVALID));
            }
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.REFRESH_TOKEN_EXPIRED));
        }

        // 받은 리프레시 토큰과 db에 있는 리프레시 토큰 체크 (검증)
        if (!userId.equals(userIdDb)) {
            unsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.REFRESH_TOKEN_INVALID));
        }

        // 시큐리티 검증
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userId, userId
        );
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException {

        String userId = authResult.getName();
        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(
                () -> new FilterException(ExceptionCodeSet.CREDENTIAL_FAIL)
        );

        String accessToken = BEARER + JwtUtil.createAccessToken(userId, LocalDateTime.now());
        String refreshToken = BEARER + JwtUtil.createRefreshToken(userId, LocalDateTime.now());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        JSONObject json = new JSONObject();

        json.put("userId", user.getId());
        json.put("accessToken", accessToken);
        json.put("refreshToken", refreshToken);
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