package com.devrun.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.devrun.entity.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
	
	//결제 정보가 db에 있을 경우만 환불진행 
		 @Query("SELECT p FROM PaymentEntity p WHERE p.merchant_uid = :merchant_uid")
		    PaymentEntity findByMerchantUid(@Param("merchant_uid") String merchant_uid);
		 
		 @Query("SELECT p FROM PaymentEntity p WHERE p.paid_amount = :paid_amount")
		 PaymentEntity findByPaidAmount(String imp_uid);

	


	
}
