package com.devrun.util;

import java.io.IOException;
import java.util.Enumeration;

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
		System.out.println("--------------------------------------------------");
		 System.out.println("HTTP Method: " + request.getMethod());
	        System.out.println("Request URI: " + request.getRequestURI());
	        System.out.println("Protocol: " + request.getProtocol());
	        Enumeration<String> headerNames = request.getHeaderNames();
	        System.out.println("---------------------헤더 정보-------------------");
	        while (headerNames.hasMoreElements()) {
	            String headerName = headerNames.nextElement();
	            System.out.println(headerName + ": " + request.getHeader(headerName));
	        }
	        System.out.println("---------------------파라미터 정보-------------------");
	        Enumeration<String> parameterNames = request.getParameterNames();
	        while (parameterNames.hasMoreElements()) {
	            String parameterName = parameterNames.nextElement();
	            System.out.println(parameterName + ": " + request.getParameter(parameterName));
		
		
	        }
		System.out.println("--------------------------------------------------");
		// HTTP 요청 헤더에서 헤더 값을 가져옴
	    String accessToken = request.getHeader("Access_token");
	    System.out.println("엑세스 토큰 : " + accessToken);
	    Cookie[] cookies = request.getCookies();
	    System.out.print("리프레시 쿠키 T / F: ");
	    System.out.println(cookies != null);	    
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
        	System.out.println("ExpiredJwtException e");
        	// 401 : JWT 토큰이 만료되었을 때
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
        } catch (SignatureException e) {
        	System.out.println("SignatureException e");
        	// 403 : JWT 토큰이 조작되었을 때
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Signature validation failed");
        } catch (Exception e) {
        	System.out.println("Exception e");
        	// 500 : 그 외 예외 처리
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error occurred");
        }
	        
	}
    
	private boolean isTokenValid(String accessToken, String refreshToken) {
        return accessToken == null && refreshToken == null;
    }

}
