package com.devrun.util;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devrun.service.CustomUserDetailsService;
import com.devrun.service.TokenBlacklistService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
    private CustomUserDetailsService userDetailsService;
	
	@Autowired
    private TokenBlacklistService tokenBlacklistService;
    
//    Spring Boot에서는 초기화 과정에서 컴포넌트를 주입할 때, 어플리케이션에 대한 Key/Value 형태의 설정을 클래스 내 변수에 값을 넣어주는 @Value Annotation이 존재한다.
//    이러한 설정은 application.properties 또는 application.yml 과 같은 파일에서 다음과 같은 형식으로 관리할 수 있다.
//    이러한 방식을 사용하여 아마존 서비스와 같이 다른 3rd party 서비스를 사용할 때 Access Key 또는 Secret Key 같은 설정을 유용하게 할 수 있다.
//    또한, Spring Boot는 Profile 별로 설정 파일을 분리하여 관리할 수 있다. 이와 같이 설정 파일에 정의한 값을 사용하기 위하여 Spring Boot에서는 @Value annotation 을 제공하고 있다.
//    하지만, static 변수 에서 다음과 같이 @Value annotation 을 사용한다면 잘못된 결과를 초래할 수 있다.
//    이때 static 변수에 접근을 하게 된다면 항상 null 이 반환 될 것이다. 이는 static 변수에 대하여 @Value annotation 이 동작하지 않는다.
//    이를 해결하기 위해서는 static 이 아닌 setter 메소드를 추가하여 static 변수에 직접적으로 값을 넣을 수 있도록 하면 된다.
    
    private static String SECRET_KEY;
	
	@Value("${jwt.secretKey}")
	public void setSecretKey(String secretKey) {
		JwtRequestFilter.SECRET_KEY = secretKey;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	        throws ServletException, IOException {
//		String requestPath = request.getRequestURI();
		// HTTP 요청 헤더에서 헤더 값을 가져옴
	    String accessToken = request.getHeader("Access_token");
	    String encodedRefreshToken = request.getHeader("Refresh_token");
	    String refreshToken = null;
	    if (encodedRefreshToken != null) {
	    	refreshToken = new String(Base64.getDecoder().decode(encodedRefreshToken));
		}
	    System.out.println("리프레시 토큰 : " + refreshToken);
//	    String easyloginTokenHeader = request.getHeader("Easylogin_token");
		//login 경로에 대한 요청인 경우 필터를 건너뛰도록 설정합니다.
		if (
//				!"/tmi".equals(requestPath) 
//				&& !"/savePaymentInfo".equals(requestPath)
//				&& !"/token/refresh".equals(requestPath)
				accessToken == null
				&& encodedRefreshToken == null
//				&& easyloginTokenHeader == null
				) {
		    chain.doFilter(request, response);
		    return;
		}
	    
	    try {
				
	        if (accessToken != null && accessToken.startsWith("Bearer ")) {
	        	
	        	if (JWTUtil.isAlgorithmValid(accessToken)) {
	        		processToken(accessToken, "Access_token", chain, request, response);
	        	} else {
	        		
	        		response.sendError(403, "Invalid token signature algorithm");
	        		
	        	}
	            
	        } else if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
	        	
	        	System.out.println("여기냐1");
//	        	if (TokenBlacklist.isTokenBlacklisted(refreshToken)) {
	        	
        		if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
	        		
	        		// 블랙리스트에 등록된 토큰 사용
	        		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Logout user");
				}
        		if (JWTUtil.isAlgorithmValid(refreshToken)) {
        			processToken(refreshToken, "Refresh_token", chain, request, response);
        		} else {
	        		response.sendError(403, "Invalid token signature algorithm");
	        	}
	        }
		        

//	        else if (easyloginTokenHeader != null && easyloginTokenHeader.startsWith("Bearer ")) {
//	        	System.out.println("이지토큰 : " + easyloginTokenHeader.substring(7));
//	        	
//	        	validateEasyLoginToken(easyloginTokenHeader.substring(7));
//	        	
//	        }
//		        else {
//		        	// 400 : 올바르지 않은 토큰
//		            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No or invalid token provided");
//		            return;
//		        }

	        System.out.println("통과해?");
	        chain.doFilter(request, response);
	        
	    } catch (ExpiredJwtException e) {
			
	        // 401 : JWT 토큰이 만료되었을 때
	        logger.error("Token is expired", e);
	        // 2.3 릴리스 이후 SpringBoot에서 오류메시지를 포함하지 않는다는 말이 있다
	        // 로컬에서 테스트할 때는 message가 정상적으로 포함되지만 AWS EC2를 사용하면 message가 사라진다
	        // 이때는 application.properties에 server.error.include-message=always를 추가해주면 message가 정상적으로 포함된다
	        // Starting from the 2.3 version, Spring Boot doesn't include an error message on the default error page. 
	        // The reason is to reduce the risk of leaking information to a client
	        // spring boot 2.3 버전 부터는 클라이언트에 정보가 누수될까봐, 기본 에러페이지에 에러메세지를 담지 않는다고 한다.
	        // https://stackoverflow.com/questions/65019051/not-getting-message-in-spring-internal-exception
	        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
	        
	    } catch (SignatureException e) {
	        // 403 : JWT 토큰이 조작되었을 때
	        logger.error("Signature validation failed", e);
	        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Signature validation failed");
	        
	    } catch (Exception e) {
	        // 500 : 그 외 예외 처리
	        logger.error("Unexpected server error occurred", e);
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error occurred");
	    }   
	}

	private void processToken(String tokenHeader, String headerName, FilterChain chain, HttpServletRequest request, HttpServletResponse response)
		    throws ServletException, IOException {
		
    	// 각각의 헤더 값이 "Bearer "로 시작하는 경우, 실제 토큰을 추출
    	String jwt = tokenHeader.substring(7);
    	System.out.println(jwt + "잘리냐");
    	String username = extractUsername(jwt);
    	
    	// 이전에 SecurityContextHolder에 저장된 토큰값과 유저정보를 초기화
    	SecurityContextHolder.clearContext();
    	System.out.println("여기냐2" + username);
    	
        // 토큰에서 추출한 아이디가 null이 아니고, 현재 Security Context에 인증 정보가 없는 경우
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
//            CustomUserDetails userDetails = (CustomUserDetails) loadedUserDetails;
            System.out.println("여기냐3" + userDetails);
            
            if (headerName.equals("Access_token")) {
	            // 토큰이 유효한 경우 Security Context에 인증 정보를 설정
	            if (validateAccessToken(jwt, userDetails)) {
	            	System.out.println("여기냐4" + validateAccessToken(jwt, userDetails));
	            	setAuthenticationInSecurityContext(userDetails, request);
	            }
	            
            } else if(headerName.equals("Refresh_token")) {

        	    if(!validateRefreshToken(jwt, userDetails)) {
        	        throw new SignatureException("Invalid refresh token");
        	        
        	    } else if(validateRefreshToken(jwt, userDetails)) {
        	    	setAuthenticationInSecurityContext(userDetails, request);
        	    }
        	}
        }
	}


    // Access 토큰에서 아이디를 추출하는 메서드
    private String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    // Access 토큰이 유효한지 검증하는 메서드
    private Boolean validateAccessToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Access 토큰의 만료 여부를 확인하는 메서드
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    // Access 토큰에서 만료 시간을 추출하는 메서드
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Access 토큰에서 클레임을 추출하는 메서드
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Access 토큰에서 모든 클레임을 추출하는 메서드
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }
    
    // Refresh 토큰의 유효성을 검사하는 메소드
    private Boolean validateRefreshToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isRefreshTokenExpired(token));
    }

    // Refresh 토큰의 만료 여부를 확인하는 메소드
    private Boolean isRefreshTokenExpired(String token) {
        return extractRefreshTokenExpiration(token).before(new Date());
    }

    // Refresh 토큰에서 만료 시간을 추출하는 메소드
    private Date extractRefreshTokenExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    // EasyLogin_token에서 userId와 email을 추출하는 메소드
//    public static String getUserIdFromEasyloginToken(String token) {
//        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//        return claims.getSubject();
//    }
//
//    public static String getEmailFromEasyloginToken(String token) {
//        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
//        return claims.get("email", String.class);
//    }

    // EasyLogin_token을 검증하는 메소드
//    private Boolean validateEasyLoginToken(String token) {
//        return !isTokenExpired(token);
//    }
    
    public void setAuthenticationInSecurityContext(UserDetails userDetails, HttpServletRequest request) {
    	// UsernamePasswordAuthenticationToken은 Spring Security에서 제공하는 Authentication의 구현체로
    	// 사용자의 인증 정보를 나타냄 이 객체는 주로 사용자의 ID, 비밀번호, 그리고 권한 정보를 포함
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken 
                = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(
        		// new WebAuthenticationDetailsSource().buildDetails(request)는 요청에 대한 세부 정보를 생성하는 역할. 이 정보는 후속 보안 작업에서 사용
        		new WebAuthenticationDetailsSource().buildDetails(request));
        System.out.println(usernamePasswordAuthenticationToken + "너냐5");
        // SecurityContext에 Authentication 객체를 설정하는 역할. Authentication 객체는 Spring Security의 다른 부분에서 현재 사용자의 인증 정보를 접근하는데 사용
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

}