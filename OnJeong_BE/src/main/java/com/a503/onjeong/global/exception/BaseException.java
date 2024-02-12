package com.a503.onjeong.global.exception;

import org.springframework.http.HttpStatus;

public interface BaseException {
    ExceptionCodeSet getExceptionCode();
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
}
