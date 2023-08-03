package com.devrun.controller;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.devrun.dto.PaymentDTO;
import com.devrun.entity.MemberEntity;
import com.devrun.entity.PaymentEntity;
import com.devrun.entity.PointEntity;
import com.devrun.repository.MemberEntityRepository;
import com.devrun.repository.PaymentInfo;
import com.devrun.repository.PaymentRepository;
import com.devrun.repository.PointRepository;
import com.devrun.service.MemberService;
import com.devrun.service.PaymentService;


@RestController
public class PaymentController {
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PointRepository pointRepository;
	
	@Autowired
	private MemberEntityRepository memberEntityRepository;
	
	@Autowired
	private MemberService memberService;

	// 결제 정보 db에 저장
		@PostMapping("/savePaymentInfo")
		public ResponseEntity<String> savePaymentInfo(@RequestBody List<PaymentDTO> paymentDTOList) {
			
			LocalDateTime dateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss", new Locale("ko"));
			String paymentDate = dateTime.format(formatter);
			System.err.println(paymentDTOList);
			
			  // 사용자의 포인트 정보를 조회
			//2개이상 구매시 구매자는 이름이 같으니깐, 첫번째 배열에 있는 이름으로 point찾음.
		    String name = paymentDTOList.get(0).getBuyer_name();
		    System.err.println(name);
		    PointEntity pointEntity = pointRepository.findByMemberEntity_name(name);
		    System.err.println(pointEntity);

		    if (pointEntity == null) {
		        // 사용자의 포인트 정보가 없을 경우 에러 응답 반환
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Point information not found for the user");
		    }

		    // 사용자의 포인트에서 지불할 금액 계산
		    int userPoint = 0;
		    for (PaymentDTO paymentDTO : paymentDTOList) {
		        userPoint += paymentDTO.getMypoint();
		    }

		    int nowPoint = pointEntity.getMypoint();

		    if (nowPoint < userPoint) {
		        // 사용자의 포인트가 부족하여 처리할 수 없는 경우 에러 응답 반환
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient points");
		    }

		    // 사용자의 포인트에서 지불할 금액만큼 차감
		    int updatedPoint = nowPoint - userPoint;
		    pointEntity.setMypoint(updatedPoint);
		    pointRepository.save(pointEntity);
		    
		    //외래키에 값 넣어주기.
		    //결제 정보 사용자 이름으로 memberEntity에서 찾은후, 밑에 추가해주기. 
		    //외래키가 user_no지만 memberEntity로 정의해서 저렇게 넣어줘야함.
		    MemberEntity memberEntity = memberEntityRepository.findByName(name);
		    System.err.println(memberEntity);
			
			try {			
				List<PaymentEntity> paymentList = new ArrayList<>();
		        for (PaymentDTO paymentDTO : paymentDTOList) {
		        PaymentEntity paymentEntity = new PaymentEntity();
		        paymentEntity.setName(paymentDTO.getName());
		        paymentEntity.setPaid_amount(paymentDTO.getPaid_amount());
	            paymentEntity.setBuyer_email(paymentDTO.getBuyer_email());
	            paymentEntity.setBuyer_name(paymentDTO.getBuyer_name());
	            paymentEntity.setImp_uid(paymentDTO.getImp_uid());
	            paymentEntity.setMerchant_uid(paymentDTO.getMerchant_uid());
	            paymentEntity.setReceipt_url(paymentDTO.getReceipt_url());
	            paymentEntity.setBuyer_tel(paymentDTO.getBuyer_tel()); 
				paymentEntity.setPaymentDate(paymentDate);
				paymentEntity.setStatus("0");	
				System.out.println(paymentEntity);	
				paymentEntity.setMemberEntity(memberEntity);
				
	            paymentList.add(paymentEntity);   
		       }
		        
		        System.err.println(paymentList);
				paymentService.savePaymentInfo(paymentList);				
				
				return ResponseEntity.ok("결제 정보가 성공적으로 저장되었습니다.");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장에 실패했습니다.");
			}
		}	
		
		//구매 정보 페이지
		
		@GetMapping("/PaymentInfo")
		public ResponseEntity<?> tmi(HttpServletRequest request) {
			
		    // refreshToken이 헤더에 있는지 확인
		    String accessToken = request.getHeader("Access_token");

		    // Refresh Token 존재 여부 확인 (null 혹은 빈문자열 인지 확인)
		    if (accessToken == null || accessToken.isEmpty()) {
		        // 400 : Access token 없음
		        return new ResponseEntity<>("Access token is required", HttpStatus.BAD_REQUEST);
		    }

		    String id = memberService.getIdFromToken(request);
		    if (memberService.isUserIdEquals(id)) {
		        MemberEntity member = memberService.findById(id);	
		        
		        String name = member.getName();

		        // 사용자의 이름으로 결제 정보 조회

		        List<PaymentInfo> payments = paymentRepository.findAllProjectedBy(name);
		        System.err.println(payments);

		        if (payments.isEmpty()) {
		            // 결제 정보가 없을 경우에 대한 처리
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No payment information found for the user");
		        }

		        return ResponseEntity.ok(payments);
		    } else {
		        // 401 토큰의 사용자와 요청한 사용자 불일치
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized request");
		    }
		}

		
	
}
