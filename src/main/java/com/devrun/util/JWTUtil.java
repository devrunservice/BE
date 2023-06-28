package com.devrun.util;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {

    private static String SECRET_KEY
//    = "D2mypmbWSgHfj7pkblhE45DfZNIV0ZksSdWoAxXnahA="
    ;
	
    @Value("${jwt.secretKey}")
    public void setSecretKey(String secretKey) {
    	JWTUtil.SECRET_KEY = secretKey;
    }
    
	// 토큰 만료시간 설정
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
}