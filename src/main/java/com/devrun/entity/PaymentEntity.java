package com.devrun.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Comment;

import com.devrun.youtube.Lecture;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "Payment")
public class PaymentEntity {		

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pay_no", length = 5)
	@Comment("결제 번호")
	private int pay_no;
	
	@Column(name = "buyerEmail", nullable = true, length = 20)
	@Comment("구매자 이메일")
	private String buyer_email;

	@Column(name = "buyerName", nullable = true, length = 20)
	@Comment("구매자 이름")
	private String buyer_name;

	@Column(name = "buyerTel", nullable = true, length = 20)
	@Comment("구매자 연락처")
	private String buyer_tel;
	
	@Column(name = "name", nullable = true, length = 20)
	@Comment("강의 이름")
	private String name;
	
	@Column(name = "paidAmount", nullable = true, length = 20)
	@Comment("강의 가격")
	private int paid_amount;
	
	@Column(name = "merchant_uid", nullable = true, length = 30)
	@Comment("아임포트 고유번호")
	private String merchant_uid;
	
	@Column(name = "impUid", nullable = true, length = 30)
	@Comment("아임포트 고유번호")
	private String imp_uid;	
	
	@Column(name = "payment_Date", nullable = true, length = 30)
	@Comment("결제 일자")
	private String paymentDate;
	
	@Column(name = "receipt_url", nullable = true, length = 255)
	@Comment("전자 영수증")
	private String receipt_url;
	
	// 주고 받는 형식이 Json이라 boolean X
	@Column(name = "status", nullable = true, length = 1)
	@Comment("환불 상태 /환불완료:1/기본:0")
	private String status;
	
	//외래 키가 대상이 되는 테이블에 있는 경우
	@ManyToOne	
	@JoinColumn(name = "userNo") //외래키 컬럼명
	private MemberEntity memberEntity; //주 테이블의 PK값	

	@ManyToOne
	@JoinColumn(name = "lectureid")
	private Lecture lecture;
	
		
		
	
	

}
