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
        
//		String requestPath = request.getRequestURI();
		// HTTP 요청 헤더에서 헤더 값을 가져옴
	    String accessToken = request.getHeader("Access_token");
	    System.out.println("엑세스 토큰 : " + accessToken);
//	    String encodedRefreshToken = request.getHeader("Refresh_token");
//	    String refreshToken = null;
//	    if (encodedRefreshToken != null) {
//	    	refreshToken = new String(Base64.getDecoder().decode(encodedRefreshToken));
//		}
	    Cookie[] cookies = request.getCookies();
	    System.out.println("리프레시 쿠키 : " + cookies);
	    
	    String refreshToken = JWTUtil.getRefreshTokenFromCookies(cookies);
	    System.out.println("리프레시 토큰 : " + refreshToken);
	    
//	    String easyloginTokenHeader = request.getHeader("Easylogin_token");
		//login 경로에 대한 요청인 경우 필터를 건너뛰도록 설정합니다.
	    
		if (isTokenValid(accessToken, refreshToken)
//				!"/tmi".equals(requestPath) 
//				&& !"/savePaymentInfo".equals(requestPath)
//				&& !"/token/refresh".equals(requestPath)
//				accessToken == null
//				&& refreshToken == null
//				&& easyloginTokenHeader == null
				) {
			System.out.println("그냥 통과");
		    chain.doFilter(request, response);
		    return;
		}
		
//	    try {
//	    	
//	        if (accessToken != null && accessToken.startsWith("Bearer ")) {
//	        	// 사용자 식별
//	        	String id = JWTUtil.getUserIdFromToken(accessToken);
//	        	// 요청에서 jti 추출
//	        	String requestJti = JWTUtil.getJtiFromToken(accessToken);
//	        	// 저장소에서 해당 사용자 ID의 jti 읽기
//	        	String storedJti = redisCache.getJti(id);
//	        	
//	        	if (requestJti.equals(storedJti)) {
//	        		
//	        		if (JWTUtil.isAlgorithmValid(accessToken)) {
//	        			processToken(accessToken, "Access_token", chain, request, response);
//	        		} else {
//	        			
//	        			response.sendError(403, "Invalid token signature algorithm");
//	        			
//	        		}
//	        	} else {
//	        		// 중복 로그인 처리
//	        		response.sendError(403, "Duplicate login detected");
//	        	}
//	        	
//	            
//	        } else if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
//	        	// 사용자 식별
//	        	String id = JWTUtil.getUserIdFromToken(accessToken);
//	        	// 요청에서 jti 추출
//	        	String requestJti = JWTUtil.getJtiFromToken(accessToken);
//	        	// 저장소에서 해당 사용자 ID의 jti 읽기
//	        	String storedJti = redisCache.getJti(id);
//	        	
//	        	if (requestJti.equals(storedJti)) {
//		        	System.out.println("여기냐1");
//	//	        	if (TokenBlacklist.isTokenBlacklisted(refreshToken)) {
//	        		if (redisCache.isTokenBlacklisted(refreshToken)) {
//		        		
//		        		// 블랙리스트에 등록된 토큰 사용
//		        		response.sendError(HttpServletResponse.SC_FORBIDDEN, "Logout user");
//					} else {
//						
//						if (JWTUtil.isAlgorithmValid(refreshToken)) {
//							processToken(refreshToken, "Refresh_token", chain, request, response);
//						} else {
//							response.sendError(403, "Invalid token signature algorithm");
//						}
//					}
//	        	} else {
//	        		// 중복 로그인 처리
//	        		response.sendError(403, "Duplicate login detected");
//	        	}
//	        }
//		        
//
////	        else if (easyloginTokenHeader != null && easyloginTokenHeader.startsWith("Bearer ")) {
////	        	System.out.println("이지토큰 : " + easyloginTokenHeader.substring(7));
////	        	
////	        	validateEasyLoginToken(easyloginTokenHeader.substring(7));
////	        	
////	        }
////		        else {
////		        	// 400 : 올바르지 않은 토큰
////		            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No or invalid token provided");
////		            return;
////		        }
//
//	        System.out.println("통과해?");
//	        chain.doFilter(request, response);
//	        
//	    } catch (ExpiredJwtException e) {
//			
//	        // 401 : JWT 토큰이 만료되었을 때
//	        logger.error("Token is expired", e);
//	        // 2.3 릴리스 이후 SpringBoot에서 오류메시지를 포함하지 않는다는 말이 있다
//	        // 로컬에서 테스트할 때는 message가 정상적으로 포함되지만 AWS EC2를 사용하면 message가 사라진다
//	        // 이때는 application.properties에 server.error.include-message=always를 추가해주면 message가 정상적으로 포함된다
//	        // Starting from the 2.3 version, Spring Boot doesn't include an error message on the default error page. 
//	        // The reason is to reduce the risk of leaking information to a client
//	        // spring boot 2.3 버전 부터는 클라이언트에 정보가 누수될까봐, 기본 에러페이지에 에러메세지를 담지 않는다고 한다.
//	        // https://stackoverflow.com/questions/65019051/not-getting-message-in-spring-internal-exception
//	        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
//	        
//	    } catch (SignatureException e) {
//	        // 403 : JWT 토큰이 조작되었을 때
//	        logger.error("Signature validation failed", e);
//	        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Signature validation failed");
//	        
//	    } catch (Exception e) {
//	        // 500 : 그 외 예외 처리
//	        logger.error("Unexpected server error occurred", e);
//	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error occurred");
//	    }   
//	}
//
//	private void processToken(String tokenHeader, String headerName, FilterChain chain, HttpServletRequest request, HttpServletResponse response)
//		    throws ServletException, IOException {
//		
//    	// 각각의 헤더 값이 "Bearer "로 시작하는 경우, 실제 토큰을 추출
//    	String jwt = tokenHeader.substring(7);
//    	System.out.println(jwt + "잘리냐");
//    	String username = extractUsername(jwt);
//    	
//    	// 이전에 SecurityContextHolder에 저장된 토큰값과 유저정보를 초기화
////    	SecurityContextHolder.clearContext();
//    	System.out.println("여기냐2" + username);
//    	
//        // 토큰에서 추출한 아이디가 null이 아니고, 현재 Security Context에 인증 정보가 없는 경우
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
////            CustomUserDetails userDetails = (CustomUserDetails) loadedUserDetails;
//            System.out.println("여기냐3" + userDetails);
//            
//            if (headerName.equals("Access_token")) {
//	            // 토큰이 유효한 경우 Security Context에 인증 정보를 설정
//	            if (validateAccessToken(jwt, userDetails)) {
//	            	System.out.println("여기냐4" + validateAccessToken(jwt, userDetails));
//	            	setAuthenticationInSecurityContext(userDetails, request);
//	            }
//	            
//            } else if(headerName.equals("Refresh_token")) {
//
//        	    if(!validateRefreshToken(jwt, userDetails)) {
//        	        throw new SignatureException("Invalid refresh token");
//        	        
//        	    } else if(validateRefreshToken(jwt, userDetails)) {
//        	    	setAuthenticationInSecurityContext(userDetails, request);
//        	    }
//        	}
//        }
		
		try {
			System.out.println("여기냐1");
			jwtUtil.handleToken(request, response, chain, accessToken, refreshToken);
        } catch (ExpiredJwtException e) {
        	// 401 : JWT 토큰이 만료되었을 때
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token is expired");
        } catch (SignatureException e) {
        	// 403 : JWT 토큰이 조작되었을 때
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Signature validation failed");
        } catch (Exception e) {
        	// 500 : 그 외 예외 처리
        	System.out.println("500");
        	jwtUtil.sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected server error occurred");
        }
		
	}
    
	private boolean isTokenValid(String accessToken, String refreshToken) {
        return accessToken == null && refreshToken == null;
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

}
