package com.devrun.service;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.stereotype.Service;


@Service
public class CacheService {

    private final LoadingCache<String, String> smsCodeCache;

    public CacheService() {
        smsCodeCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // 5분 후에 만료 지금은 핸드폰 인증에만 사용 중
                .maximumSize(100) // 최대 100개의 항목 저장
                .build(key -> null); // 만약 키가 존재하지 않으면 null 반환
    }

    public void saveSmsCode(String phoneNumber, String code) {
        smsCodeCache.put(phoneNumber, code);
    }

    public String getSmsCode(String phoneNumber) {
        return smsCodeCache.get(phoneNumber);
    }

    public void removeSmsCode(String phoneNumber) {
        smsCodeCache.invalidate(phoneNumber);
    }
    
    public void saveEmailVerifyTempKey(String id, String key) {
    	smsCodeCache.put(id, key);
    }
    
    public String getEmailVerifyTempKey(String id) {
        return smsCodeCache.get(id);
    }
    
    public void removeEmailVerifyTempKey(String id) {
        smsCodeCache.invalidate(id);
    }
}
