package com.a503.onjeong.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> handleUserException(UserException e) {
        return makeResponseFormat(e.getExceptionCode());
    }

    @ExceptionHandler(WeatherException.class)
    public ResponseEntity<ErrorResponse> handleWeatherException(WeatherException e) {
        return makeResponseFormat(e.getExceptionCode());
    }

    @ExceptionHandler(KakaoException.class)
    public ResponseEntity<ErrorResponse> handleCommonException(KakaoException e) {
        return makeResponseFormat(e.getExceptionCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return makeResponseFormat(ExceptionCodeSet.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> makeResponseFormat(ExceptionCodeSet exceptionCode) {
        return ResponseEntity.status(exceptionCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .code(exceptionCode.getCode())
                        .message(exceptionCode.getMessage())
                        .build()
                );
    }

}
