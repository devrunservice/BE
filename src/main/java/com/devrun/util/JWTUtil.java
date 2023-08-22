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
    
    // 시그니쳐 알고리즘 설정
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    
	// 토큰 만료시간 설정
    private static final long EASYLOGIN_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;		// 5분
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;		// 15분				테스트는 1초로 할 것
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;	// 24시간, 24시간/일 * 60분/시간 * 60초/분 * 1000밀리초/초
    
    // ACCESS_TOKEN 생성
    public static String generateAccessToken(String userId, String name) {
    		   // 새로운 JWT를 생성하기 위한 Builder 객체를 초기화
        return Jwts.builder()
        		// JWT의 sub (subject) 필드를 설정합니다. 이 필드는 토큰이 대상이 되는 주체(일반적으로 사용자)를 식별
                .setSubject(userId)
                // 추가적인 claim으로 name을 설정. claim은 추가적인 데이터를 저장할 수 있는 key-value 쌍
                .claim("name", name)
                // WT의 jti (JWT ID) 필드를 설정합니다. 이 필드는 토큰의 고유 식별자로 사용됩니다. 이를 통해 토큰이 한 번만 사용되도록 할 수 있으며, 랜덤한 UUID를 생성하여 이를 ID로 사용
                .setId(UUID.randomUUID().toString())  // 랜덤한 UUID를 jti (JWT ID)로 사용
                // JWT의 exp (expiration time) 필드를 설정
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                // WT를 서명하는 데 사용될 알고리즘과 시크릿 키를 설정
                .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
                // 최종적으로 생성된 JWT를 직렬화하여 문자열 형태로 반환
                .compact();
    }
    
    // REFRESH_TOKEN 생성
    public static String generateRefreshToken(String userId, String name) {
        return Jwts.builder()
            .setSubject(userId)
//            .claim("name", name)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
            .compact();
    }
    
    // 주어진 token이 유효한지 확인합니다. 유효하면 true를, 그렇지 않으면 false를 반환
    public static boolean validateToken(String token) {
        try {
        	String subToken = token.substring(7);
        	
        	// Jws는 JWT (JSON Web Token)의 서명된 부분을 나타냄
        	// Jws<Claims>는 서명을 포함한 토큰의 전체 구조를 나타냄
        	// Jws 객체는 헤더 (알고리즘, 타입 등), 본문 (클레임), 서명 세 부분을 포함
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken);
            return claims.getBody().getExpiration().after(new Date());
        } catch (Exception e) {
        	System.out.println("로그아웃 에러 메시지 : " + e);
        	e.printStackTrace();
            return false;
        }
    }

    // 주어진 token으로부터 사용자 ID를 추출
    public static String getUserIdFromToken(String token) {
    	String subToken = token.substring(7);
    	
    	// Claims는 JWT의 페이로드 (Payload)를 나타내는 본문 부분
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken).getBody();
        System.out.println("사용자 아이디 : " + claims.getSubject());
        return claims.getSubject();
    }
    
    // 주어진 token으로부터 alg를 추출하여 검증
    public static boolean isAlgorithmValid(String token) {
        String subToken = token.substring(7); // Bearer 제거
        Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken);
        String algorithmUsed = jwsClaims.getHeader().getAlgorithm();
        return SIGNATURE_ALGORITHM.getValue().equals(algorithmUsed);
    }

    
    // 토큰을 두 가지 동시에 사용할 경우 주어진 token으로부터 사용자 ID를 추출
    // 아직 사용하지 않음
//    public static String getUserIdFromToken(String accessToken, String refreshToken) {
//    	
//    	String token;
//    	
//    	if (accessToken != null && refreshToken == null) {
//			token = accessToken;
//		} else if (refreshToken != null && accessToken == null) {
//			token = refreshToken;
//		} else {
//			// 두 토큰이 모두 null인 경우에 대한 처리
//			throw new IllegalArgumentException("Token missing or at least one");
//		}
//    	
//    	String subToken = token.substring(7);
//        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken).getBody();
//        System.out.println("사용자 아이디 : " + claims.getSubject());
//        return claims.getSubject();
//    }
    
    // EASYLOGIN_TOKEN 생성
    public static String generateEasyloginToken(String userId, String email) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .setId(UUID.randomUUID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + EASYLOGIN_TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
    
    public static String getEmailFromEasyloginToken(String token) {
    	String subToken = token.substring(7);
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken).getBody();
        return claims.get("email", String.class);
    }

}
