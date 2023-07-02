package com.devrun.util;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1 day in milliseconds, 시간 * 분* 초 * 밀리초 = 1일 * 1시간 * 1분 * 1초

    public static String generateToken(String id, String name) {
    		   // 새로운 JWT를 생성하기 위한 Builder 객체를 초기화
        return Jwts.builder()
        		// JWT의 sub (subject) 필드를 설정합니다. 이 필드는 토큰이 대상이 되는 주체(일반적으로 사용자)를 식별
                .setSubject(id)
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
}