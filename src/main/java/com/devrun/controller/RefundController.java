package com.devrun.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devrun.service.RefundService;


@RestController
public class RefundController {
	
	@Autowired
	private RefundService refundService;
	
	@Value("${IAMPORT_KEY}")
	private String KEY;
	
	@Value("${IAMPORT_SECRET}")
	private String SECRET;	
	
	
	 // 사용자 환불 로직
	 @PostMapping("/payment")
	 public ResponseEntity<String> refundPay(@RequestBody Map<String, Object> refundData) {
	     try {
	         refundService.refundPayment(refundData, KEY, SECRET);
	         refundService.saveRefund(refundData);	         
	         return ResponseEntity.ok("환불이 성공적으로 처리되었습니다.");
	     } catch (Exception e) {
	    	 
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("환불 처리 중에 오류가 발생했습니다.");
	     }
	 }

}
