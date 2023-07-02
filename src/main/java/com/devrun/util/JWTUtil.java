package com.devrun.util;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {

    private static String SECRET_KEY;
	
    @Value("${jwt.secretKey}")
    public void setSecretKey(String secretKey) {
    	JWTUtil.SECRET_KEY = secretKey;
    }
    
	// 토큰 만료시간 설정
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;				// refresh token을 만들면 ACCESS_TOKEN_EXPIRATION_TIME으로 대체
//    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;		// 15분				테스트는 1초로 할 것
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;	// 24시간, 24시간/일 * 60분/시간 * 60초/분 * 1000밀리초/초
    
    // ACCESS_TOKEN 생성
    public static String generateToken(String userId, String name) {
    		   // 새로운 JWT를 생성하기 위한 Builder 객체를 초기화
        return Jwts.builder()
        		// JWT의 sub (subject) 필드를 설정합니다. 이 필드는 토큰이 대상이 되는 주체(일반적으로 사용자)를 식별
                .setSubject(userId)
                // 추가적인 claim으로 name을 설정. claim은 추가적인 데이터를 저장할 수 있는 key-value 쌍
                .claim("name", name)
                // WT의 jti (JWT ID) 필드를 설정합니다. 이 필드는 토큰의 고유 식별자로 사용됩니다. 이를 통해 토큰이 한 번만 사용되도록 할 수 있으며, 랜덤한 UUID를 생성하여 이를 ID로 사용
                .setId(UUID.randomUUID().toString())  // 랜덤한 UUID를 jti (JWT ID)로 사용
                // JWT의 exp (expiration time) 필드를 설정
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // WT를 서명하는 데 사용될 알고리즘과 시크릿 키를 설정
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                // 최종적으로 생성된 JWT를 직렬화하여 문자열 형태로 반환
                .compact();
    }
    
    // REFRESH_TOKEN 생성
    public static String generateRefreshToken(String userId) {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
            .compact();
    }
    
    // 주어진 token이 유효한지 확인합니다. 유효하면 true를, 그렇지 않으면 false를 반환
    public static boolean validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return claims.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // 메서드는 주어진 refresh token으로부터 사용자 ID를 추출
    public static String getUserIdFromRefreshToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    
}