package com.devrun.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Comment;
import lombok.Data;

@Data
@Entity
@Table(name = "Payment")
public class PaymentEntity {		

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_no", length = 5)
	@Comment("유저 구분 번호")
	private int user_no;
	
	@Column(name = "buyerEmail", nullable = true, length = 100)
	@Comment("구매자 이메일")
	private String buyer_email;

	@Column(name = "buyerName", nullable = true, length = 50)
	@Comment("구매자 이름")
	private String buyer_name;

	@Column(name = "buyerTel", nullable = true, length = 20)
	@Comment("구매자 연락처")
	private String buyer_tel;
	
	@Column(name = "paidAmount", nullable = true, length = 20)
	@Comment("상품 가격")
	private int paid_amount;
	
	@Column(name = "merchant_uid", nullable = true, length = 100)
	@Comment("주문번호")
	private String merchant_uid;
	
	@Column(name = "impUid", nullable = true, length = 100)
	@Comment("결제고유번호")
	private String imp_uid;	
	
	@Column(name = "payment_Date", nullable = true, length = 100)
	@Comment("결제 일자")
	private String paymentDate;
	
	
	
	

}
