package com.devrun.controller;

import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.devrun.entity.PaymentEntity;
import com.devrun.service.PaymentService;


@RestController
public class PaymentController {
	private final PaymentService paymentService;
	
	@Value("${iamport.KEY}")
	private String KEY;
	
	@Value("${iamport.SECRET_Key}")
	private String SECRET;
	

	
	 public PaymentController(PaymentService paymentService) {
	        this.paymentService = paymentService;
	    }
	 
	 @PostMapping("/savePaymentInfo")
	 public void savePaymentInfo(@RequestBody PaymentEntity paymentEntity) {
		 paymentService.savePaymentInfo(paymentEntity);
		 
	 }
	 
	 
	 @PostMapping("/payment")
	 public void refundPay(@RequestBody Map<String,Object> refundData) {
		  
		 //rest방식으로 api 호출 준비
		 RestTemplate restTemplate = new RestTemplate();
		 
		 // HTTP 요청의 헤더 정보를 설정하기 위한 객체를 생성
		 HttpHeaders headers = new HttpHeaders();
		 
		 // Json형식으로 설정
		 headers.setContentType(MediaType.APPLICATION_JSON);
		 
		 // body 생성
		 JSONObject body = new JSONObject();
		 
		 // body 에 key,secret 넣어주기
		 body.put("imp_key", KEY);		 
		 body.put("imp_secret", SECRET);
		
		 try {
			 
			 HttpEntity<JSONObject>entity = new HttpEntity<>(body, headers);
			 
			 // 아임포트 서버에 토큰 요청 
			 ResponseEntity<JSONObject> fullToken = restTemplate.postForEntity("https://api.iamport.kr/users/getToken", entity, JSONObject.class);
			 
			 System.out.println(fullToken + "fullToken");	 
			 System.out.println(refundData);			 
			 System.out.println(KEY);
			 System.out.println(SECRET);
			 
			 //토큰 중에서 access_token만 받는 형변환
			 JSONObject token2 = fullToken.getBody();
			 JSONParser jsonParser = new JSONParser();
			 JSONObject jsonObject = (JSONObject) jsonParser.parse(token2.toString());
			 JSONObject response = (JSONObject) jsonObject.get("response");
			 String access = (String) response.get("access_token");
			 System.err.println(access);
   		 
	            headers.clear();
	            
	            //API에 접속하기 위해서는 access token을 API 서버에 제출해서 인증을 해야 합니다. 
	            //이 때 사용하는 인증 방법이 Bearer Authentication 입니다. 이 방법은 OAuth를 위해서 고안된 방법이고, RFC 6750에 표준명세서가 있습니다.		        
	            headers.setBearerAuth(access);
		      
		        body.clear();
		        
		        // refundData 에서 merchant_uid 받아오기 
		        // 향후 수정될수있음. merchant_uid 나 imp_uid 둘중하나 넣어주면 됩니다.
	            body.put("merchant_uid", refundData.get("merchant_uid"));

		        HttpEntity<JSONObject> RefundEntity = new HttpEntity<>(body, headers);
		        // 아임포트 api 문서와 동일한 url로 결제 취소 요청.
		        restTemplate.postForEntity("https://api.iamport.kr/payments/cancel", RefundEntity, JSONObject.class);
		        //완료 			 
		        
		        System.out.println("결제 취소가 완료되었습니다.");
			
		} catch (Exception e) {
			
		    System.err.println("결제 취소 중 오류가 발생했습니다: " + e.getMessage());


		}
		 
		 
	 }
	 

}
