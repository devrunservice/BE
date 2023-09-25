package com.devrun.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.devrun.util.JwtRequestFilter;

@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	return http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                .antMatchers("/tmi").hasAnyAuthority("STUDENT", "MENTO")
                .antMatchers("/users/login-info").authenticated()
                .antMatchers("/authz/token/refresh").authenticated()
                .antMatchers("/authz/logout").permitAll()
                .anyRequest().permitAll()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .logout().disable()
            .build();
    }
//    기본적으로 스프링 시큐리티는 인증 후 사용자 세션을 생성하고 그 세션에 사용자 정보를 저장합니다. 
//    그러나 SessionCreationPolicy.STATELESS를 설정하면 스프링 시큐리티는 세션을 사용하지 않으며, 따라서 사용자 정보도 세션에 저장되지 않습니다.
//    이 설정은 주로 RESTful API와 같은 상태가 없는(stateless) 애플리케이션에서 사용됩니다. 
//    이러한 애플리케이션에서는 모든 요청이 자체적으로 인증 정보를 포함해야 하며, 서버는 클라이언트의 이전 요청에 대한 정보를 저장하지 않습니다.
//    따라서 SessionCreationPolicy.STATELESS를 사용하면 UserDetails 객체는 세션에 저장되지 않으며, 요청마다 인증 토큰을 다시 확인해야 합니다. 
//    이를 위해 매 요청마다 헤더나 쿠키에서 토큰을 읽어 인증을 수행해야 할 것입니다.
    
}