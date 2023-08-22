package com.devrun.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    			// 모든 경로에 대해 CORS 설정을 적용
        registry.addMapping("/**")
                // 모든 출처에서 오는 요청을 허용
				.allowedOrigins("https://devrun.net")
//				.allowedOrigins("*")
                // 모든 HTTP 메소드(GET, POST, PUT, DELETE 등)를 허용
        		.allowedMethods("*")
        		// 쿠키 전송 허용
        		.allowCredentials(true)
        		// 모든 헤더 허용
        		.allowedHeaders("*");
    }
}