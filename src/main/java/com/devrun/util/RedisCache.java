package com.devrun.util;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisCache {
	
    private final StringRedisTemplate redisTemplate;

    public RedisCache(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 토큰을 blacklist에 추가
    public void blacklistToken(String token) {
    	
    	// Redis의 opsForValue() 메소드를 사용하여 토큰을 키로, "blacklisted"를 값으로 저장
        redisTemplate.opsForValue().set(token, "blacklisted");
        
        // Redis의 만료 시간을 24시간으로 설정
        redisTemplate.expire(token, 24, TimeUnit.HOURS);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
    	
    	// Redis의 hasKey() 메소드를 사용하여 토큰이 Redis에 저장되어 있는지 확인
        return redisTemplate.hasKey(token);
    }
    
    // jti를 이용해 로그인 처리
    public void setJti(String id, String jti) {
    	System.out.println("redis id : " + id);
    	System.out.println("redis jti : " + jti);
    	redisTemplate.opsForValue().set(id, jti);
    	redisTemplate.expire(id, 15, TimeUnit.MINUTES);
    }
    
    // jti를 이용해 로그아웃 처리
    public void removeJti(String id) {
        redisTemplate.delete(id);
    }

    // 해당 id에 연결된 jti 값을 가져옴
    public String getJti(String id) {
    	String getjti = redisTemplate.opsForValue().get(id);
    	System.out.println("redis getjti : " + getjti);
        return getjti;
    }
}