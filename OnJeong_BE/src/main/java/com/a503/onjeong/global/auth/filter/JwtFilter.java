package com.a503.onjeong.global.auth.filter;

import com.a503.onjeong.domain.user.repository.UserRepository;
import com.a503.onjeong.global.auth.service.AuthService;
import com.a503.onjeong.global.exception.ExceptionCodeSet;
import com.a503.onjeong.global.exception.FilterException;
import com.a503.onjeong.global.util.JwtUtil;
import com.sun.jdi.InternalException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/* 매 request마다 JWT 검증하는 필터 */
public class JwtFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private static final String BEARER = "Bearer ";

    public JwtFilter(AuthenticationManager authenticationManager,
                     UserRepository userRepository,
                     AuthenticationEntryPoint authenticationEntryPoint,
                     AuthService authService) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        accessToken = accessToken.substring(BEARER.length());
        String userId = null;

        // 액세스 토큰 검증
        try{
            if (JwtUtil.verify(accessToken)) {
                userId = JwtUtil.getSubject(accessToken);
                if (!userRepository.existsById(Long.valueOf(userId)))
                    onUnsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.ACCESS_TOKEN_INVALID));
            }
        } catch (Exception e){
            onUnsuccessfulAuthentication(request, response, new FilterException(ExceptionCodeSet.ACCESS_TOKEN_EXPIRED));
        }

        // 필터 통과
        UserDetails userDetails = authService.loadUserByUsername(userId);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        SecurityContextHolder.clearContext();
        try {
            authenticationEntryPoint.commence(request, response, failed);
        } catch (ServletException e) {
            throw new FilterException(ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        }
    }
}