package com.a503.onjeong.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ExceptionCodeSet {

    // 유저
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "1000", "회원이 존재하지 않습니다."),

    //인증,인가
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "2000", "권한이 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "2001", "액세스 토큰이 만료 되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "2002", "리프레시 토큰이 만료 되었습니다."),
    ACCESS_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "2003", "엑세스 토큰이 유효하지 않습니다"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "2004", "리프레시 토큰이 유효하지 않습니다."),
    CREDENTIAL_FAIL(HttpStatus.UNAUTHORIZED, "2005", "인증에 실패했습니다."),

    // 날씨
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, "3000", "존재하지 않는 주소입니다."),

    // 카카오
    KAKAO_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "4000", "카카오 액세스 토큰이 만료되었습니다."),
    KAKAO_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "4001", "카카오 리프레시 토큰이 만료되었습니다."),
    KAKAO_FAILED(HttpStatus.BAD_REQUEST, "4002", "카카오 API 호출에 실패했습니다."),

    // 게임
    GAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "5000", "존재하지 않는 게임입니다"),

    //ETC
    OK(HttpStatus.OK, "0000", "성공적으로 동작하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "9000", "서버 동작 중 에러가 발생했습니다."),;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;


    public static ExceptionCodeSet findExceptionByCode(String code) {
        for (ExceptionCodeSet exceptionCode : ExceptionCodeSet.values()) {
            if (exceptionCode.getCode().equals(code)) return exceptionCode;
        }
        return null;
    }

    public static ExceptionCodeSet findExceptionByMsg(String msg) {
        for (ExceptionCodeSet exceptionCode : ExceptionCodeSet.values()) {
            if (exceptionCode.getMessage().equals(msg)) return exceptionCode;
        }
        return null;
    }
}
