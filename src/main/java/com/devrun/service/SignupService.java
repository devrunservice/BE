package com.devrun.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.devrun.entity.MemberEntity;
import com.devrun.repository.MemberEntityRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SignupService {

	@Value("${sens.accessKey}")
	private String accessKey;
	
	@Value("${sens.secretKey}")
	private String secretKey;
	
	@Value("${sens.serviceId}")
	private String serviceId;

	private final MemberEntityRepository memberEntityRepository;
	
	private final Map<String, Integer> phoneCodeMap = new ConcurrentHashMap<>();

	public MemberEntity findById(String id) {
		return memberEntityRepository.findById(id);
	}
	
	public void insert(MemberEntity memberEntity) {
		memberEntityRepository.save(memberEntity);
	}
	
	public int checkID(String id) {
		return memberEntityRepository.countById(id);
	}

	public int checkEmail(String email) {
		return memberEntityRepository.countByEmail(email);
	}
	
	public int checkphone(String phonenumber) {
		return memberEntityRepository.countByPhonenumber(phonenumber);
	}
	
	public Mono<String> sendSmsCode(String recipientPhoneNumber) {
        WebClient webClient = WebClient.create("https://sens.apigw.ntruss.com");

        String method = "POST";
        String uri = "/sms/v2/services/" + serviceId + "/messages";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String message = method + " " + uri + "\n" + timestamp + "\n" + accessKey;
        String signature;
        try {
            signature = makeSignature(message, secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return Mono.error(e);
        }

        // 발신 번호를 적절한 값으로 설정하세요.
        String senderPhoneNumber = "01062037049";
        
        // 6자리 인증코드 생성
        Random r = new Random();
        int smsCode = r.nextInt(900000) + 100000;

        // Save the code for verification later
        phoneCodeMap.put(recipientPhoneNumber, smsCode);
        
        String jsonBody = "{"
                + "\"type\": \"SMS\","
                + "\"contentType\": \"COMM\","
                + "\"from\": \"" + senderPhoneNumber + "\","
                + "\"subject\": \"회원가입 인증코드\","
                + "\"content\": \"DEVRUN 인증코드: " + smsCode + "\","
                + "\"messages\": ["
                + "    {"
                + "        \"to\": \"" + recipientPhoneNumber + "\""
                + "    }"
                + "]"
                + "}";


        Mono<String> result = webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("x-ncp-apigw-timestamp", timestamp)
                .header("x-ncp-iam-access-key", accessKey)
                .header("x-ncp-apigw-signature-v2", signature)
                .body(BodyInserters.fromValue(jsonBody))
                .retrieve()
                .bodyToMono(String.class);
        return result;
    }
	
	private static String makeSignature(String message, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
	    SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	    Mac mac = Mac.getInstance("HmacSHA256");
	    mac.init(signingKey);

	    return Base64.getEncoder().encodeToString(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
	}

    public boolean verifySmsCode(String phoneNumber, int code) {
        Integer savedCode = phoneCodeMap.get(phoneNumber);
        System.out.println(savedCode + ":" + code);
        return savedCode != null && savedCode.equals(code);
    }
    
    public boolean validateId(String id) {
    	// 영어, 숫자를 포함한 5자 이상 13자 이하
    	Pattern pattern = Pattern.compile("^(?=.[a-zA-Z])(?=.[0-9])[a-zA-Z0-9]{5,13}$", Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(id);
    	return matcher.find();
    }
    
    public boolean validateEmail(String email) {
    	// 이메일 형식
        Pattern pattern = Pattern.compile("^[^\\s@]+@[^\\s@]+.[^\\s@]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }

    public boolean validatePassword(String password) {
    	// 숫자, 영문, 특수문자를 포함한 8자 이상 15이하
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&]).{8,15}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }
}