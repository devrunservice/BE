package com.devrun.controller;


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
	
	 public PaymentController(PaymentService paymentService) {
	        this.paymentService = paymentService;
	    }
	 
	//결제 정보 db에 저장
	 @PostMapping("/savePaymentInfo")
	 public ResponseEntity<String> savePaymentInfo(@RequestBody PaymentEntity paymentEntity) {
	     try {
	         paymentService.savePaymentInfo(paymentEntity);
	         return ResponseEntity.ok("결제 정보가 성공적으로 저장되었습니다.");
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 정보 저장에 실패했습니다.");
	     }
	 }

	 

}
