package com.devrun.service;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.devrun.entity.Consent;
import com.devrun.entity.Contact;
import com.devrun.entity.LoginInfo;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PointEntity;
import com.devrun.repository.ConsentRepository;
import com.devrun.repository.ContactRepository;
import com.devrun.repository.LoginInfoRepository;
import com.devrun.repository.MemberEntityRepository;
import com.devrun.util.CaffeineCache;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Value("${sens.accessKey}")
    private String accessKey;

    @Value("${sens.secretKey}")
    private String secretKey;

    @Value("${sens.serviceId}")
    private String serviceId;

    private final MemberEntityRepository memberEntityRepository;
    private final ContactRepository contactRepository;
    private final ConsentRepository consentRepository;
    private final LoginInfoRepository loginInfoRepository;
    
    private final CaffeineCache cacheService; // 캐시 서비스 주입

//    private final Map<String, String> phoneCodeMap = new ConcurrentHashMap<>();

    public MemberEntity findById(String id) {
        return memberEntityRepository.findById(id);
    }

    public PointEntity insert(PointEntity point) {
        return memberEntityRepository.save(point);
    }

    public MemberEntity insert(MemberEntity memberEntity) {
		return memberEntityRepository.save(memberEntity);
	}
    
    public Contact insert(Contact contact) {
		return contactRepository.save(contact);
	}
    
    public Consent insert(Consent consent) {
		return consentRepository.save(consent);
	}

	public LoginInfo insert(LoginInfo loginInfo) {
		return loginInfoRepository.save(loginInfo);
	}

    public int checkID(String id) {
        return memberEntityRepository.countById(id);
    }

    public int checkEmail(String email) {
        return contactRepository.countByEmail(email);
    }

    public int checkphone(String phonenumber) {
        return contactRepository.countByPhonenumber(phonenumber);
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
        String smsCode = Integer.toString(r.nextInt(900000) + 100000);

        // Save the code for verification later
        cacheService.saveCaffeine(recipientPhoneNumber, smsCode); // Caffeine 캐시에 코드 저장


        String jsonBody = "{"
                + "\"type\": \"SMS\","
                + "\"contentType\": \"COMM\","
                + "\"from\": \"" + senderPhoneNumber + "\","
                + "\"subject\": \"인증코드 발송\","
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
        System.out.println("sms인증 : " + result);
        return result;
    }

    public Mono<String> sendSmsFindId(String recipientPhoneNumber, String id) {
        WebClient webClient = WebClient.create("https://sens.apigw.ntruss.com");
        System.out.println("여기냐1");
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
        System.out.println("여기냐2" + id);

        // 발신 번호를 적절한 값으로 설정하세요.
        String senderPhoneNumber = "01062037049";

        String jsonBody = "{"
                + "\"type\": \"SMS\","
                + "\"contentType\": \"COMM\","
                + "\"from\": \"" + senderPhoneNumber + "\","
                + "\"subject\": \"아이디 찾기\","
                + "\"content\": \"DEVRUN에 가입하신 아이디는 " + id + " 입니다.\","
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
        System.out.println("결과");
        return result;
    }

    private static String makeSignature(String message, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        return Base64.getEncoder().encodeToString(mac.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    public boolean verifyCode(String key, String code) {
//        String savedCode = phoneCodeMap.get(phoneNumber);
    	String savedCode = cacheService.getCaffeine(key); // Caffeine 캐시에서 코드 검색
        System.out.println(savedCode + ":" + code);
        return savedCode != null && savedCode.equals(code);
    }

    public void removeVerifyCode(String key) {
//        phoneCodeMap.remove(phoneNumber);
    	cacheService.removeCaffeine(key); // Caffeine 캐시에서 코드 제거
    }

    public boolean validateId(String id) {
        // 영어, 숫자를 포함한 5자 이상 13자 이하
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{5,13}$", Pattern.CASE_INSENSITIVE);
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
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).{8,15}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

	public List<MemberEntity> findByIdContains(String keyword) {
		// TODO Auto-generated method stub
		return memberEntityRepository.findByIdContains(keyword);
	}

//    public boolean isUserIdEquals(String id) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userId = authentication.getName();
//        return userId.equals(id);
//    }
}