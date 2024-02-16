package com.a503.onjeong.global.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.jdi.InternalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;

/* JWT 생성 및 유효성 검사 util */
@Component
public class JwtUtil {
    private static String SECRET_KEY;
    private static String ISSUER;
    private static final String TYPE = "type";

    private static final long ACCESS_TIME = 60 * 3600;  // 액세스 토큰 6시간
    private static final long REFRESH_TIME = 60 * 60 * 24 * 55;  // 리프레시 토큰 약 2달

    @Value("${jwt.secret_key}")
    public void setSecretKey(String secretKey) {
        JwtUtil.SECRET_KEY = secretKey;
    }

    @Value("${jwt.issuer}")
    public void setIssuer(String issuer) {
        JwtUtil.ISSUER = issuer;
    }

    /* 액세스 토큰 생성 */
    public static String createAccessToken(String id, LocalDateTime created) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(ACCESS_TIME)))
                .withIssuedAt(Date.from(Instant.now()))
                .withNotBefore(Date.valueOf(created.toLocalDate()))
                .withSubject(id)
                .withClaim(TYPE, "access")
                .sign(ALGORITHM);
    }

    /* 리프레시 토큰 생성 */
    public static String createRefreshToken(String id, LocalDateTime created) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(Date.from(Instant.now().plusSeconds(REFRESH_TIME)))
                .withNotBefore(Date.valueOf(created.toLocalDate()))
                .withSubject(id)
                .withClaim(TYPE, "refresh")
                .sign(ALGORITHM);
    }

    /* 토큰 검증 함수 */
    public static boolean verify(String token) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);
        DecodedJWT verify;
        try {
            verify = JWT.require(ALGORITHM).build().verify(token);
        } catch (Exception e) {
            throw new InternalException("토큰 검증 실패 예외");
        }

        if (!verify.getIssuer().equals(ISSUER)) throw new InternalException("잘못된 iss 예외");
        else if (verify.getExpiresAt().before(Date.from(Instant.now()))) throw new InternalException("잘못된 exp 예외");
        else if (verify.getNotBefore().after(Date.from(Instant.now()))) throw new InternalException("잘못된 nbf 예외");

        return true;
    }

    /* subject 추출 함수 */
    public static String getSubject(String token) {
        final Algorithm ALGORITHM = Algorithm.HMAC256(SECRET_KEY);

        DecodedJWT decodedToken = JWT.require(ALGORITHM).build().verify(token);
        return decodedToken.getSubject();
    }


}
