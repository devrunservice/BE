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
	@Column(name = "refundNo", length = 11)
	@Comment("환불 번호")
	private int refundNo;	

	@Column(name = "merchantUid", nullable = true, length = 30)
	@Comment("아임포트 고유번호")
	private String merchant_uid;
	
	@Column(name = "name", nullable = true, length = 20)
	@Comment("강의 이름")
	private String name;

	@Column(name = "amount", nullable = true, length = 20)
	@Comment("환불 금액")
	private int amount;
	
	@Column(name = "payment_Date", nullable = true, length = 30)
	@Comment("환불 일자")
	private String refunddate;
	
	
	 public RefundEntity(String merchant_uid, int amount, String refunddate, String name) {
	        this.merchant_uid = merchant_uid;
	        this.amount = amount;	        
	        this.refunddate = refunddate;
	        this.name = name;
	    }
}
