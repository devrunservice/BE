package com.devrun.controller;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.devrun.entity.PaymentEntity;
import com.devrun.repository.PaymentRepository;
import com.devrun.service.PaymentService;


@RestController
public class PaymentController {
	@Autowired
	private PaymentService paymentService;
	
	@Autowired
	private PaymentRepository paymentRepository;

	// 결제 정보 db에 저장
	@PostMapping("/savePaymentInfo")
	public ResponseEntity<String> savePaymentInfo(@RequestBody PaymentEntity paymentEntity) {
		try {
			LocalDateTime dateTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm:ss", new Locale("ko"));
			String paymentDate = dateTime.format(formatter);
			paymentEntity.setPaymentDate(paymentDate);

			paymentEntity.setStatus("0");

			paymentService.savePaymentInfo(paymentEntity);
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
