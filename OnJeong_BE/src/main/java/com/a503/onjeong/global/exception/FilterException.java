package com.a503.onjeong.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class FilterException extends AuthenticationException implements BaseException {

    private ExceptionCodeSet exceptionCode;

    public FilterException(ExceptionCodeSet exceptionCode) {
        super(exceptionCode.getCode());
        this.exceptionCode = exceptionCode;
    }

    public FilterException(String errorMessage) {
        super(errorMessage);
    }

    @Override
    public ExceptionCodeSet getExceptionCode() {
        return this.exceptionCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.exceptionCode.getHttpStatus();
    }

    @Override
    public String getCode() {
        return this.exceptionCode.getCode();
    }
}
