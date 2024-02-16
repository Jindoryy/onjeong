package com.a503.onjeong.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/* 인증 과정에서 발생한 AuthenticationException 예외처리 클래스 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        ExceptionCodeSet exception = ExceptionCodeSet.findExceptionByCode(authException.getMessage());
        request.getRequestURL();

        //인증 실패
        if (authException.getClass().equals(BadCredentialsException.class)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            setResponse(response, ExceptionCodeSet.CREDENTIAL_FAIL);
        } else if (authException.getClass().equals(InsufficientAuthenticationException.class) || exception == null) {  //Anonymous user || 잡히지 않은 에러
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            setResponse(response, ExceptionCodeSet.INTERNAL_SERVER_ERROR);
        } else {
            response.setStatus(exception.getHttpStatus().value());
            setResponse(response, exception);
        }
    }

    // message, code 형태로 저장
    private void setResponse(HttpServletResponse response, ExceptionCodeSet exceptionCode) throws IOException {
        ErrorResponse responseFormat = ErrorResponse.builder()
                .message(exceptionCode.getMessage())
                .code(exceptionCode.getCode())
                .build();

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseFormat));
    }
}
