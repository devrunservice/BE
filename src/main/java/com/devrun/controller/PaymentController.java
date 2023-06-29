package com.devrun.controller;

import java.util.Map;


import org.springframework.beans.factory.annotation.Value;
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
	 @PostMapping("/savePaymentInfo")
	 public void savePaymentInfo(@RequestBody PaymentEntity paymentEntity) {
		 paymentService.savePaymentInfo(paymentEntity);
		 
	 }
	 
	 // 사용자 환불 로직
	 @PostMapping("/payment")
	 public void refundPay(@RequestBody Map<String,Object> refundData) {
		 paymentService.refundPayment(refundData, KEY, SECRET);
		 
	 }
	 

}
