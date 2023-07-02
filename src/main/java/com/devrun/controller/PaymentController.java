package com.devrun.controller;

import java.util.Map;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.devrun.entity.PaymentEntity;
import com.devrun.service.PaymentService;


@RestController
public class PaymentController {
	private final PaymentService paymentService;
	
	@Value("${iamport_KEY}")
	private String KEY;
	
	@Value("${iamport_SECRET}")
	private String SECRET;
	

	
	 public PaymentController(PaymentService paymentService) {
	        this.paymentService = paymentService;
	    }
	 
	 //결제 정보 db에 저장
	 @PostMapping("/savePayment")
	 public ResponseEntity<String> savePaymentInfo(@RequestBody PaymentEntity paymentEntity) {
	     try {
	         paymentService.savePaymentInfo(paymentEntity);
	         return ResponseEntity.ok("결제 정보가 성공적으로 저장되었습니다.");
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장에 실패했습니다.");
	     }
	 }
	 
	 // 사용자 환불 로직
	 @PostMapping("/payment")
	 public ResponseEntity<String> refundPay(@RequestBody Map<String, Object> refundData) {
	     try {
	         paymentService.refundPayment(refundData, KEY, SECRET);
	         return ResponseEntity.ok("환불이 성공적으로 처리되었습니다.");
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("환불 처리 중에 오류가 발생했습니다.");
	     }
	 }

	 

}
