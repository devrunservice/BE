package com.devrun.util;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import com.devrun.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {

    private static String SECRET_KEY;
	
    @Value("${jwt.secretKey}")
    public void setSecretKey(String SECRET_KEY) {
    	JWTUtil.SECRET_KEY = SECRET_KEY;
    }
    
//  Spring Boot에서는 초기화 과정에서 컴포넌트를 주입할 때, 어플리케이션에 대한 Key/Value 형태의 설정을 클래스 내 변수에 값을 넣어주는 @Value Annotation이 존재한다.
//  이러한 설정은 application.properties 또는 application.yml 과 같은 파일에서 다음과 같은 형식으로 관리할 수 있다.
//  이러한 방식을 사용하여 아마존 서비스와 같이 다른 3rd party 서비스를 사용할 때 Access Key 또는 Secret Key 같은 설정을 유용하게 할 수 있다.
//  또한, Spring Boot는 Profile 별로 설정 파일을 분리하여 관리할 수 있다. 이와 같이 설정 파일에 정의한 값을 사용하기 위하여 Spring Boot에서는 @Value annotation 을 제공하고 있다.
//  하지만, static 변수 에서 다음과 같이 @Value annotation 을 사용한다면 잘못된 결과를 초래할 수 있다.
//  이때 static 변수에 접근을 하게 된다면 항상 null 이 반환 될 것이다. 이는 static 변수에 대하여 @Value annotation 이 동작하지 않는다.
//  이를 해결하기 위해서는 static 이 아닌 setter 메소드를 추가하여 static 변수에 직접적으로 값을 넣을 수 있도록 하면 된다.
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private CaffeineCache redisCache;
//    private RedisCache redisCache;
    
    // 시그니쳐 알고리즘 설정
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    
	// 토큰 만료시간 설정
    private static final long EASYLOGIN_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;		// 5분
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 15 * 60 * 1000;		// 15분				테스트는 1초로 할 것
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;	// 24시간, 24시간/일 * 60분/시간 * 60초/분 * 1000밀리초/초
    
    // jti 생성
    public static String generateJti() {
        return UUID.randomUUID().toString();
    }
    
    // ACCESS_TOKEN 생성
    public static String generateAccessToken(String userId, String name, String jti) {
    	// 새로운 JWT를 생성하기 위한 Builder 객체를 초기화
        return Jwts.builder()
        		// JWT의 sub (subject) 필드를 설정합니다. 이 필드는 토큰이 대상이 되는 주체(일반적으로 사용자)를 식별
                .setSubject(userId)
                // 추가적인 claim으로 name을 설정. claim은 추가적인 데이터를 저장할 수 있는 key-value 쌍
                .claim("name", name)
                // WT의 jti (JWT ID) 필드를 설정합니다. 이 필드는 토큰의 고유 식별자로 사용됩니다. 이를 통해 토큰이 한 번만 사용되도록 할 수 있으며, 랜덤한 UUID를 생성하여 이를 ID로 사용
                .setId(jti)  // 랜덤한 UUID를 jti (JWT ID)로 사용
                // JWT의 exp (expiration time) 필드를 설정
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                // WT를 서명하는 데 사용될 알고리즘과 시크릿 키를 설정
                .signWith(SIGNATURE_ALGORITHM, SECRET_KEY)
                // 최종적으로 생성된 JWT를 직렬화하여 문자열 형태로 반환
                .compact();
    }
    
    // REFRESH_TOKEN 생성
    public static String generateRefreshToken(String userId, String name, String jti) {
        return Jwts.builder()
            .setSubject(userId)
//            .claim("name", name)
            .setId(jti)
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
        Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
        String algorithmUsed = jwsClaims.getHeader().getAlgorithm();
        return SIGNATURE_ALGORITHM.getValue().equals(algorithmUsed);
    }

    // 주어진 token으로부터 jti 추출
    public static String getJtiFromToken(String token) {
    	String subToken = token.substring(7);
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken).getBody();
        return claims.getId(); // jti (JWT ID) 반환
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
    
    // EasyloginToken에서 email 추출
    public static String getEmailFromEasyloginToken(String token) {
    	String subToken = token.substring(7);
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(subToken).getBody();
        return claims.get("email", String.class);
    }
    
    // Access_token과 Refresh_token 나눠서 처리
    public void handleToken(HttpServletRequest request, HttpServletResponse response, FilterChain chain, String accessToken, String refreshToken)
            throws ServletException, IOException {
        if (validateAndHandleToken(accessToken, "Access_token", request, response, chain)) return;
        if (validateAndHandleToken(refreshToken, "Refresh_token", request, response, chain)) return;
        // 올바르지 않은 토큰
        sendErrorResponse(response, 403, "Invalid token");
    }
 	
    // 오류 응답 전송
    public void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.sendError(status, message);
    }
 	
    // 토큰 유효성 검사 및 처리
 	private boolean validateAndHandleToken(String token, String tokenType, HttpServletRequest request, HttpServletResponse response, FilterChain chain)
 	        throws ServletException, IOException {
 		
 	    if (token != null && token.startsWith("Bearer ")) {
 	        String jwt = token.substring(7);
 	        String userId = getUserIdFromToken(token);
 	        String requestJti = getJtiFromToken(token);
 	        String storedJti = redisCache.getJti(userId);
 	        
 	        if ( !isValidAlgorithm(jwt, response) 
 	        		|| isBlacklistedRefreshToken(tokenType, token, response)
 	        		) return true;
 	        if (isValidateJti(requestJti, storedJti, userId)) {
				if (validateAndProcessToken(token, request)) {
					// 토큰 검증 및 처리 성공
					chain.doFilter(request, response);
					return true;
				}
 	        } else {
 	            // 중복 로그인 처리
 	            sendErrorResponse(response, 403, "Duplicate login detected");
 	            return true;
 	        }
 	    }
 	    return false;
 	}
 	
 	// 중복 로그인 방지를 위해 jti 검증
 	private boolean isValidateJti(String requestJti, String storedJti, String userId) throws IOException {
 		if (storedJti == null || requestJti.equals(storedJti)) {
 			// redis에 jti 등록
			redisCache.setJti(userId, requestJti);
			return true;
		}
 		return false;
 	}
 	
 	// 주어진 token으로부터 alg를 추출하여 검증
 	private boolean isValidAlgorithm(String token, HttpServletResponse response) throws IOException {
 	    if (!JWTUtil.isAlgorithmValid(token)) {
 	    	// 잘못된 서명 알고리즘
 	        sendErrorResponse(response, 403, "Invalid token signature algorithm");
 	        return false;
 	    }
 	    return true;
 	}
 	
	// 블랙리스트에 등록된 토큰인지 검증
	private boolean isBlacklistedRefreshToken(String tokenType, String token, HttpServletResponse response) throws IOException {
	    if (tokenType.equals("Refresh_token") && redisCache.isTokenBlacklisted(token)) {
	    	// 블랙리스트에 등록된 토큰 사용
	        sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Logout user");
	        return true;
	    }
	    return false;
	}
	
	// 토큰을 검증하고 인증 프로세스를 처리
	private boolean validateAndProcessToken(String token, HttpServletRequest request) {
		String subToken = token.substring(7);
	    if (token != null && token.startsWith("Bearer ")) {
	        String username = extractUsername(subToken);
	        // 사용자 이름이 null이 아니고, 현재 Security Context에 인증 정보가 없는 경우
	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        	// 사용자 정보 로드
	            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

	            // 토큰이 유효한 경우 Security Context에 인증 정보 설정
	            if (validateToken(subToken, userDetails)) {
	            	// 인증 정보를 Security Context에 설정
	                setAuthenticationInSecurityContext(userDetails, request);
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	// Security Context에 사용자 인증 정보를 설정
    public void setAuthenticationInSecurityContext(UserDetails userDetails, HttpServletRequest request) {
    	// UsernamePasswordAuthenticationToken은 Spring Security에서 제공하는 Authentication의 구현체로
    	// 사용자의 인증 정보를 나타냄 이 객체는 주로 사용자의 ID, 비밀번호, 그리고 권한 정보를 포함
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // 요청에 대한 세부 정보 설정 (예: IP 주소, 세션 ID 등)
        usernamePasswordAuthenticationToken.setDetails(
        		// new WebAuthenticationDetailsSource().buildDetails(request)는 요청에 대한 세부 정보를 생성하는 역할. 이 정보는 후속 보안 작업에서 사용
        		new WebAuthenticationDetailsSource().buildDetails(request));
        // SecurityContext에 Authentication 객체를 설정하는 역할. Authentication 객체는 Spring Security의 다른 부분에서 현재 사용자의 인증 정보를 접근하는데 사용
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
	
	// 토큰에서 아이디를 추출하는 메서드
    private String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }
    
    // 토큰의 유효성을 검사하는 메소드
    private Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 토큰의 만료 여부를 확인하는 메소드
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 토큰에서 만료 시간을 추출하는 메소드
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 토큰에서 클레임을 추출하는 메서드
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 토큰에서 모든 클레임을 추출하는 메서드
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
 	
 	// 쿠키에서 refreshToken 반환
    public static String getRefreshTokenFromCookies(Cookie[] cookies) {
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Refresh_token".equals(cookie.getName())) {
                    String encodedRefreshToken = cookie.getValue();
                    refreshToken = new String(Base64.getDecoder().decode(encodedRefreshToken));
                    break;
                }
            }
        }
        return refreshToken; // 쿠키에서 "Refresh_token"을 찾지 못한 경우 null 반환
    }

}
