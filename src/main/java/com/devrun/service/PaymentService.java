package com.devrun.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devrun.entity.PaymentEntity;
import com.devrun.repository.PaymentRepository;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;
	

	public void savePaymentInfo(List<PaymentEntity> paymentList) {
		System.err.println("----서비스----");
		System.err.println(paymentList);
		paymentRepository.saveAll(paymentList);
		
	}	

		
	}
    
   

