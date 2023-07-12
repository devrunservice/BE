package com.devrun.util;

import java.io.IOException;
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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
    private CustomUserDetailsService userDetailsService;
    
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
		// HTTP 요청 헤더에서 헤더 값을 가져옴
	    String accessTokenHeader = request.getHeader("Access_token");
	    String refreshTokenHeader = request.getHeader("Refresh_token");
	    String easyloginTokenHeader = request.getHeader("Easylogin_token");

	    
	    try {
	        if (accessTokenHeader != null && accessTokenHeader.startsWith("Bearer ")) {
	            processToken(accessTokenHeader, chain, request, response);
	        }
	        
	        if (refreshTokenHeader != null && refreshTokenHeader.startsWith("Bearer ")) {
	            processToken(refreshTokenHeader, chain, request, response);
	        }
	        
	        if (easyloginTokenHeader != null && easyloginTokenHeader.startsWith("Bearer ")) {
	            processToken(easyloginTokenHeader, chain, request, response);
	        }
	        chain.doFilter(request, response);
	        
	    } catch (io.jsonwebtoken.ExpiredJwtException e) {
	    	
	        // 401 : 이 부분은 JWT 토큰의 유효기간이 만료된 경우에 실행됩니다.
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("Token has expired");
	        
	    } catch (io.jsonwebtoken.SignatureException e) {
	    	
	        // 401 : 이 부분은 JWT 토큰의 서명이 유효하지 않은 경우에 실행됩니다. 
	        // 서명은 JWT 토큰의 내용이 변조되지 않았음을 보증하는 요소입니다.
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("Invalid token signature");
	        
	    } catch (io.jsonwebtoken.MalformedJwtException e) {
	    	
	        // 401 : 이 부분은 JWT 토큰의 구조가 잘못된 경우에 실행됩니다. 
	        // 예를 들어, JWT 토큰의 헤더, 페이로드, 서명의 구조가 적절하지 않을 경우 발생합니다.
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("Malformed token");
	        
	    } catch (Exception e) {
	    	
	        // 500 : 이 부분은 위의 세 가지 예외 이외에 발생할 수 있는 모든 예외를 처리합니다. 
	        // 이는 서버 내부의 에러, 예기치 못한 상황, 그 외의 다른 예외 상황에 대비한 것입니다.
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("Internal server error");
	        
	    }
	    
	}

	private void processToken(String tokenHeader, FilterChain chain, HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		// 각각의 헤더 값이 "Bearer "로 시작하는 경우, 실제 토큰을 추출
	    String jwt = tokenHeader.substring(7);
	    String username = extractUsername(jwt);
	    
	    // 이전에 SecurityContextHolder에 저장된 토큰값과 유저정보를 초기화
	    SecurityContextHolder.clearContext();
	    
	    // 토큰에서 추출한 아이디가 null이 아니고, 현재 Security Context에 인증 정보가 없는 경우
	    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
	        
	        // 토큰이 유효한 경우 Security Context에 인증 정보를 설정
	        if (validateToken(jwt, userDetails)) {
	            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
	                    userDetails, null, userDetails.getAuthorities());
	            usernamePasswordAuthenticationToken
	                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	        }
	    }
	}

    // 토큰에서 아이디를 추출하는 메서드
    private String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    // 토큰이 유효한지 검증하는 메서드
    private Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // 토큰의 만료 여부를 확인하는 메서드
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    // 토큰에서 만료 시간을 추출하는 메서드
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
}