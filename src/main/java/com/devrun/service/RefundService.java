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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.devrun.dto.FulltokenDTO;
import com.devrun.dto.RefundDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PaymentEntity;
import com.devrun.entity.PointEntity;
import com.devrun.entity.PointHistoryEntity;
import com.devrun.entity.RefundEntity;
import com.devrun.repository.PaymentRepository;
import com.devrun.repository.PointHistoryRepository;
import com.devrun.repository.PointRepository;
import com.devrun.repository.RefundRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RefundService {
	@Autowired
	 private RefundRepository refundRepository;
	@Autowired
	 private PaymentRepository paymentRepository;
	@Autowired
	 private PointRepository pointRepository;
	@Autowired
	 private MemberService memberService;
	@Autowired
	 private PointHistoryRepository pointHistoryRepository;

    public void refundPayment(RefundDTO refundDTO, String KEY, String SECRET) throws Exception {        
    	try {
    		
            // refundData에서 merchant_uid 확인
    		System.err.println(refundDTO);
    		System.err.println(KEY);
    		System.err.println(SECRET);
            String merchantUid = refundDTO.getMerchant_uid();
            String name= refundDTO.getName();
            System.err.println(merchantUid);

           // 결제 정보 조회
//            PaymentEntity paymentEntity = paymentRepository.findByMerchantUid(merchantUid);
            List<PaymentEntity> paymentEntity = paymentRepository.findByListMerchantUidAndName(merchantUid, name);

            System.err.println(paymentEntity);

            if (paymentEntity.isEmpty()) {
                throw new Exception("해당 거래 번호 또는 상품 이름이 없습니다.");
            }
                
            System.err.println("해당 거래 번호에 대한 결제 정보가 존재합니다.");         
            
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
	            body.put("merchant_uid", refundDTO.getMerchant_uid());
	            body.put("amount", refundDTO.getAmount());	            
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
    public void saveRefund(RefundDTO refundDTO) {
        try {      	        	
            String merchantUid = refundDTO.getMerchant_uid();
            int amount = refundDTO.getAmount();
            String name = refundDTO.getName();            
            System.out.println(merchantUid);            
            //날짜 세팅
        	LocalDateTime dateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss", new Locale("ko"));
			String refundDate = dateTime.format(formatter);
            
            // 부분 환불 상태 처리
            // 2개 이상 결제 시 거래번호가 중복되므로 list로 불러옴
            List<PaymentEntity> paymentEntities = paymentRepository.findByListMerchantUidAndName(merchantUid, name);
            System.err.println(paymentEntities);
            
            // for문을 돌려서 앞에서 날려준 amount 부분환불값을 찾아주어서 맞는 배열로 로직 진행.
            for (PaymentEntity paymentEntity : paymentEntities) {
                try {
                    if (paymentEntity.getPaid_amount() == amount && paymentEntity.getName().equals(name)) {
                        System.err.println(paymentEntity.getPaid_amount());
                        System.err.println(amount);
                        
                        // 상태를 부분환불로 변경
                        paymentEntity.setStatus("1");
                        paymentRepository.save(paymentEntity);
                        
                        RefundEntity refundEntity = new RefundEntity(merchantUid, amount, refundDate, name);
                        System.out.println(refundEntity);
                        refundRepository.save(refundEntity);
                    }                    
                } catch (Exception e) {
                    System.err.println("환불 처리 중 오류 발생: " + e.getMessage());
                }	
            }
            //사용자 찾아서 포인트 회수
            String userid = SecurityContextHolder.getContext().getAuthentication().getName();
    		MemberEntity member = memberService.findById(userid);    		
    		int usrno = member.getUserNo(); // 
    		
    		int refundpoints = (int)(amount*0.1);
		    PointEntity pointEntity = pointRepository.findByMemberEntity_userNo(usrno);
		    System.err.println(pointEntity);
		    int nowpoints = pointEntity.getMypoint();
		    int updatepoints = nowpoints - refundpoints;
		    System.err.println(updatepoints);
		    pointEntity.setMypoint(updatepoints);
		    pointRepository.save(pointEntity); 
		    
		    //포인트 히스토리 추가
			PointHistoryEntity historyEntity = new PointHistoryEntity();
            historyEntity.setMemberEntity(member);
            historyEntity.setUpdatetime(refundDate);
            historyEntity.setPointupdown(-refundpoints);
            String explanation="환불 처리 포인트 회수";
            historyEntity.setProductname(name);
            historyEntity.setExplanation(explanation);			   
            pointHistoryRepository.save(historyEntity);         
		    		    
            
        } catch (Exception ex) {
            System.err.println("환불 데이터 처리 중 오류 발생: " + ex.getMessage());
        }
    }	
		

}
