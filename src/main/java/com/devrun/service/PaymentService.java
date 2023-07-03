package com.devrun.service;


import org.springframework.stereotype.Service;
import com.devrun.entity.PaymentEntity;
import com.devrun.repository.PaymentRepository;

@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	

	
	public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public void savePaymentInfo(PaymentEntity paymentEntity) {
        paymentRepository.save(paymentEntity);
    }
    
   

}
