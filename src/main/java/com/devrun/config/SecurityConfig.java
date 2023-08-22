package com.devrun.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            .authorizeRequests(authorize -> authorize
                    .antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                    .antMatchers("/tmi").hasAnyAuthority("STUDENT","ADMIN")// 인증이 필요한 /tmi 엔드포인트
//                    .antMatchers("/savePaymentInfo").authenticated()
                    .antMatchers("/authz/token/refresh").authenticated()
                    .antMatchers("/logout").permitAll()
                .anyRequest().permitAll())

            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .logout().disable()
            .build();
    }
    
}