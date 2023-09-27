package com.devrun.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component							  // HTTP 요청에 대해 한 번만 실행되는 필터
public class JwtRequestFilter extends OncePerRequestFilter {
	
	@Autowired
    private JWTUtil jwtUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	        throws ServletException, IOException {
		System.out.println("Request URL: " + request.getRequestURL().toString());
        System.out.println("Remote Address: " + request.getRemoteAddr());
        
		// HTTP 요청 헤더에서 헤더 값을 가져옴
	    String accessToken = request.getHeader("Access_token");
	    System.out.println("엑세스 토큰 : " + accessToken);
	    Cookie[] cookies = request.getCookies();
	    System.out.println("리프레시 쿠키 : " + cookies);
	    
	    String refreshToken = JWTUtil.getRefreshTokenFromCookies(cookies);
	    System.out.println("리프레시 토큰 : " + refreshToken);
	    
		//login 경로에 대한 요청인 경우 필터를 건너뛰도록 설정합니다.
		if (isTokenValid(accessToken, refreshToken)) {
			System.out.println("그냥 통과");
		    chain.doFilter(request, response);
		    return;
		}
		
		try {
			jwtUtil.handleToken(request, response, chain, accessToken, refreshToken);
        } catch (ExpiredJwtException e) {
        	// 401 : JWT 토큰이 만료되었을 때
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
        } catch (SignatureException e) {
        	// 403 : JWT 토큰이 조작되었을 때
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Signature validation failed");
        } catch (Exception e) {
        	// 500 : 그 외 예외 처리
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error occurred");
        }
	}
    
	private boolean isTokenValid(String accessToken, String refreshToken) {
        return accessToken == null && refreshToken == null;
    }

}
