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
		paymentRepository.saveAll(paymentList);
		
	}	

//    public void savePaymentInfo(PaymentEntity paymentEntity) {
//        paymentRepository.save(paymentEntity);
//    }

	

		
	}
    
   

