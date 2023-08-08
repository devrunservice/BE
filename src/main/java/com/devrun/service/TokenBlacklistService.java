package com.devrun.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {
	
    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 토큰을 blacklist에 추가
    public void blacklistToken(String token) {
    	
    	// Redis의 opsForValue() 메소드를 사용하여 토큰을 키로, "blacklisted"를 값으로 저장
        redisTemplate.opsForValue().set(token, "blacklisted");
        
        // 토큰의 만료 시간을 24시간으로 설정
        redisTemplate.expire(token, 24, TimeUnit.HOURS);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
    	
    	// Redis의 hasKey() 메소드를 사용하여 토큰이 Redis에 저장되어 있는지 확인
        return redisTemplate.hasKey(token);
    }
}