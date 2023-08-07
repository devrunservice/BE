package com.devrun.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.devrun.dto.FulltokenDTO;
import com.devrun.entity.PaymentEntity;
import com.devrun.entity.RefundEntity;
import com.devrun.repository.PaymentRepository;
import com.devrun.repository.RefundRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;




@Service
public class RefundService {
	@Autowired
	 private RefundRepository refundRepository;
	@Autowired
	 private PaymentRepository paymentRepository;

    public void refundPayment(Map<String, Object> refundData, String KEY, String SECRET) throws Exception {        
    	try {
    		
            // refundData에서 merchant_uid 확인
            String merchantUid = refundData.get("merchant_uid").toString();
            System.err.println(merchantUid);

           // 결제 정보 조회
//            PaymentEntity paymentEntity = paymentRepository.findByMerchantUid(merchantUid);
            List<PaymentEntity> paymentEntity = paymentRepository.findByListMerchantUid(merchantUid);

            System.err.println(paymentEntity);

            if (paymentEntity.isEmpty()) {
                throw new Exception("해당 거래 번호가 없습니다.");
            }
                
            System.err.println("해당 거래 번호에 대한 결제 정보가 존재합니다.");           
            
    		System.err.println(refundData);
            System.out.println("환불 진행");
		
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
		
				   
			 
			 HttpEntity<JSONObject>entity = new HttpEntity<>(body, headers);
			 
			 // 아임포트 서버에 토큰 요청 
			 ResponseEntity<JSONObject> fullToken = restTemplate.postForEntity("https://api.iamport.kr/users/getToken", entity, JSONObject.class);
			 
			 System.out.println(fullToken + "fullToken");	 
			 System.out.println(refundData);
			 
			  // ResponseEntity에서 JSONObject 추출
			 JSONObject fullTokenObject = fullToken.getBody(); 
			  // JSONObject를 문자열로 변환
			 String jsonString = fullTokenObject.toString(); 		
						 
			 //토큰 중에서 access_token만 꺼내기 
			 //objectMapper 만들어주고
			 ObjectMapper objectMapper = new ObjectMapper();
			 
			 //JSON의 모든 데이터를 파싱하는 것이 아닌 내가 필요로 하는 데이터, 즉 내가 필드로 선언한 데이터들만 파싱할 수 있다.
			 objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
			 
			 //Json 형식에 맞게 dto 만들어주고 readValue사용
		     FulltokenDTO fulltokenDto = objectMapper.readValue(jsonString, FulltokenDTO.class);
		     
		     // 토큰 가져오기
		     System.err.println(fulltokenDto.getResponse().getAccess_token());
		     String access = fulltokenDto.getResponse().getAccess_token();
		 
	            headers.clear();
	            //API에 접속하기 위해서는 access token을 API 서버에 제출해서 인증을 해야 합니다. 
	            //이 때 사용하는 인증 방법이 Bearer Authentication 입니다. 이 방법은 OAuth를 위해서 고안된 방법이고, RFC 6750에 표준명세서가 있습니다.		        
	            headers.setBearerAuth(access);	           
	            
		      
		        body.clear();
		        
		        // refundData 에서 merchant_uid 받아오기 
		        // 향후 수정될수있음. merchant_uid 나 imp_uid 둘중하나 넣어주면 됩니다.
	            body.put("merchant_uid", refundData.get("merchant_uid"));
	            body.put("amount", refundData.get("amount"));
	            System.err.println(body);


		        HttpEntity<JSONObject> TokenEntity = new HttpEntity<>(body, headers);
		        // 아임포트 api 문서와 동일한 url로 결제 취소 요청.
		        restTemplate.postForEntity("https://api.iamport.kr/payments/cancel", TokenEntity, JSONObject.class);
		        
		       
		        
		        System.out.println("결제 취소가 완료되었습니다.");
			
    	 } catch (Exception e) {
    	        System.err.println("환불 처리 중 오류가 발생했습니다1111: " + e.getMessage());
    	        //서비스단에서 예외처리 할 경우 앞단은 컨트롤단이랑 통신하니깐 한번 더 예외처리를 해준다
    	        throw e; 
    	        

    	}
    }
		
		//사용자 환불 정보 db 저장
    public void saveRefund(Map<String, Object> refundData) {
        try {
        	
            String merchantUid = refundData.get("merchant_uid").toString();
            int amount = Integer.parseInt(refundData.get("amount").toString());
            
            System.out.println(merchantUid);
            System.out.println("들어와?");
            
            // 부분 환불 상태 처리
            // 2개 이상 결제 시 거래번호가 중복되므로 list로 불러옴
            List<PaymentEntity> paymentEntities = paymentRepository.findByListMerchantUid(merchantUid);
            System.err.println(paymentEntities);
            
            // for문을 돌려서 앞에서 날려준 amount 부분환불값을 찾아주어서 맞는 배열로 로직 진행.
            for (PaymentEntity paymentEntity : paymentEntities) {
                try {
                    if (paymentEntity.getPaid_amount() == amount) {
                        System.err.println(paymentEntity.getPaid_amount());
                        System.err.println(amount);
                        
                        // 상태를 부분환불로 변경
                        paymentEntity.setStatus("1");
                        paymentRepository.save(paymentEntity);
                        
                        LocalDateTime dateTime = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh:mm:ss", new Locale("ko"));
                        String refundDate = dateTime.format(formatter);
                        
                        RefundEntity refundEntity = new RefundEntity(merchantUid, amount, refundDate);
                        System.out.println(refundEntity);
                        System.err.println(refundData);
                        refundRepository.save(refundEntity);
                    }                    
                } catch (Exception e) {
                    System.err.println("환불 처리 중 오류 발생: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("환불 데이터 처리 중 오류 발생: " + ex.getMessage());
        }
    }	
		

}
