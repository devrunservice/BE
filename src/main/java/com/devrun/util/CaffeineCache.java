package com.devrun.util;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Service;


@Service
public class CaffeineCache {

    private final LoadingCache<String, String> smsCodeCache;

    public CaffeineCache() {
        smsCodeCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // 5분 후에 만료 지금은 핸드폰 인증에만 사용 중
                .maximumSize(100) // 최대 100개의 항목 저장
                .build(key -> null); // 만약 키가 존재하지 않으면 null 반환
    }

    public void saveCaffeine(String key, String value) {
        smsCodeCache.put(key, value);
    }

    public String getCaffeine(String key) {
        return smsCodeCache.get(key);
    }

    public void removeCaffeine(String key) {
        smsCodeCache.invalidate(key);
    }
    
    
    
    
    
    
    
    
    
    // 로컬 테스트 버전
    // 로컬 테스트 버전
    // 로컬 테스트 버전
    // 로컬 테스트 버전
    // 로컬 테스트 버전
    // 로컬 테스트 버전
    
    // 토큰을 blacklist에 추가
 	public void blacklistToken(String token) {
 		// Redis의 opsForValue() 메소드를 사용하여 토큰을 키로, "blacklisted"를 값으로 저장
 		smsCodeCache.put(token, token);
 	}
 	
 	// 토큰이 블랙리스트에 있는지 확인
 	public boolean isTokenBlacklisted(String token) {
 		// Redis의 hasKey() 메소드를 사용하여 토큰이 Redis에 저장되어 있는지 확인
 		if (smsCodeCache.get(token) != null) {
 			return true;
			
		} else {
			return false;
		}
 	}
 	  
 	// jti를 이용해 로그인 처리
 	public void setJti(String id, String jti) {
 		System.out.println("redis id : " + id);
 		smsCodeCache.put(id, jti.toString());
 		System.out.println("redis jti : " + jti);
 	}
 	  
 	// jti를 이용해 로그아웃 처리
 	public void removeJti(String id) {
 		smsCodeCache.invalidate(id);
 	}
 	
 	// 해당 id에 연결된 jti 값을 가져옴
 	public String getJti(String id) {
 		String getjti = smsCodeCache.get(id);
 		System.out.println("redis getjti : " + getjti);
 		return getjti;
 	}
}