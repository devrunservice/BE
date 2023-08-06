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
@Table(name = "Refund")
public class RefundEntity {	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_no", length = 5)
	@Comment("유저 구분 번호")
	private int user_no;	

	@Column(name = "merchantUid", nullable = true, length = 100)
	@Comment("주문번호")
	private String merchant_uid;

	@Column(name = "amount", nullable = true, length = 100)
	@Comment("환불 금액")
	private int amount;

	@Column(name = "reason", nullable = true, length = 50)
	@Comment("환불 이유")
	private String reason;		
	
	@Column(name = "payment_Date", nullable = true, length = 100)
	@Comment("환불 일자")
	private String refunddate;
	
	
	 public RefundEntity(String merchant_uid, int amount, String reason, String refunddate) {
	        this.merchant_uid = merchant_uid;
	        this.amount = amount;
	        this.reason = reason;
	        this.refunddate = refunddate;
	    }
}
