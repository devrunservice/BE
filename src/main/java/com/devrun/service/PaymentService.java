package com.devrun.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.devrun.entity.PaymentEntity;
import com.devrun.repository.PaymentRepository;

@Service
public class PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;	

    public void savePaymentInfo(PaymentEntity paymentEntity) {
        paymentRepository.save(paymentEntity);
    }
    
   

}
