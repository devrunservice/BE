package com.devrun.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AESUtil {
	
    private static String SECRET_KEY;
    
    @Value("${aes.secretKey}")
    private void setSECRET_KEY(String SECRET_KEY) {
    	AESUtil.SECRET_KEY = SECRET_KEY;
    }
    
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";	// PKCS5Padding은 블록 크기가 16이 아닐 때 패딩을 자동으로 추가 
    																// ㄴ input length must be multiple of 16 when decrypting with padded cipher 문제 해결

    public static String encrypt(String data) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getUrlEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedData));
        return new String(original);
    }
}
