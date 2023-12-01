package com.devrun.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.devrun.entity.PaymentEntity;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
	
	//결제 정보가 db에 있을 경우만 환불진행 
	@Query("SELECT p FROM PaymentEntity p WHERE p.merchant_uid = :merchant_uid AND p.name = :name")
	List<PaymentEntity> findByListMerchantUidAndName(@Param("merchant_uid") String merchant_uid, @Param("name") String name);
		 
	@Query("SELECT p FROM PaymentEntity p WHERE p.paid_amount = :paid_amount")
	PaymentEntity findByPaidAmount(@Param("paid_amount") String imp_uid);
	
	@Query(value = "SELECT p.name AS name, p.receipt_url AS receipturl, "
	        + "p.pay_no AS payno, p.merchant_uid AS merchantUid, p.paid_amount AS paidamount, p.status AS status, p.payment_date As paymentDate, "
	        + "ROW_NUMBER() OVER(ORDER BY paymentDate DESC) AS userpayno "
	        + "FROM payment p WHERE p.user_no = :usrno", nativeQuery = true)    
	Page<PaymentInfo> findAllbyPaymentEntity(@Param("usrno") int usrno, PageRequest pageRequest);	
	
	@Query("SELECT p FROM PaymentEntity p WHERE p.merchant_uid = :merchant_uid")
	PaymentEntity findByMerchantUid(@Param("merchant_uid") String merchant_uid);





	
	

      	 


	
}
