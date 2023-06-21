package com.devrun.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

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

	public void insert(MemberEntity memberEntity) {
		memberEntityRepository.save(memberEntity);
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

        Random r = new Random();
        int smsCode = r.nextInt(900000) + 100000;

        // Save the code for verification later
        phoneCodeMap.put(recipientPhoneNumber, smsCode);
        
        String jsonBody = "{"
                + "\"type\": \"SMS\","
                + "\"from\": \"" + senderPhoneNumber + "\","
                + "\"content\": \"회원가입 인증코드.\","
                + "\"messages\": ["
                + "    {"
                + "        \"to\": \"" + recipientPhoneNumber + "\","
                + "        \"content\": \"DEVRUN 인증코드 : \"" + smsCode
                + "    }"
                + "]"
                + "}";

        return webClient.post()
                .uri(uri)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("x-ncp-apigw-timestamp", timestamp)
                .header("x-ncp-iam-access-key", accessKey)
                .header("x-ncp-apigw-signature-v2", signature)
                .body(BodyInserters.fromValue(jsonBody))
                .retrieve()
                .bodyToMono(String.class);
    }
	
	private static String makeSignature(String message, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
	    SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	    Mac mac = Mac.getInstance("HmacSHA256");
	    mac.init(signingKey);

	    return Base64.getEncoder().encodeToString(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
	}

    public boolean verifySmsCode(String phoneNumber, int code) {
        Integer savedCode = phoneCodeMap.get(phoneNumber);
        return savedCode != null && savedCode.equals(code);
    }
}