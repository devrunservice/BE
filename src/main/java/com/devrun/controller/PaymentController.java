package com.devrun.controller;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.devrun.dto.PaymentDTO;
import com.devrun.entity.PaymentEntity;
import com.devrun.entity.PointEntity;
import com.devrun.repository.PaymentRepository;
import com.devrun.repository.PointRepository;
import com.devrun.service.PaymentService;


@RestController
public class PaymentController {
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	@Autowired
	private PointRepository pointRepository;

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
	            paymentList.add(paymentEntity);   
		       }
		        
		        System.err.println(paymentList);
				paymentService.savePaymentInfo(paymentList);
				
				
				

				
				return ResponseEntity.ok("결제 정보가 성공적으로 저장되었습니다.");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장에 실패했습니다.");
			}
		}
	
	// 결제 정보
	
	@GetMapping("/PaymentInfo")
	public ResponseEntity<List<PaymentEntity>> getAllPayments() {
		List<PaymentEntity> payments = paymentRepository.findAll();
		return ResponseEntity.ok(payments);
	}

}
