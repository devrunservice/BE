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

	@Column(name = "buyerAddr", nullable = true, length = 100)	
	@Comment("구매자 주소")
	private String buyer_addr;

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
	private String paid_amount;
	
	@Column(name = "merchantUid", nullable = true, length = 100)
	@Comment("주문번호")
	private String merchant_uid;
	
	@Column(name = "impUid", nullable = true, length = 100)
	@Comment("주문번호")
	private String imp_uid;
	
	@Column(name = "cancelAmount", nullable = true, length = 100)
	@Comment("환불 가격")
	private int cancel_amount;
	
	
	
	
	
//	데이터베이스에 Enum 값을 저장할 때, 일반적으로 두 가지 전략을 사용할 수 있습니다:
//
//	ORDINAL: Enum 값의 순서(0부터 시작)를 데이터베이스에 저장합니다. 
//	이 방법은 간단하고 효율적이지만, Enum에 새로운 값이 추가되면 문제가 발생할 수 있습니다. 새로운 값이 중간에 추가되면, 기존의 값들의 순서가 변경되어 잘못된 데이터를 읽어올 수 있습니다.
//
//	STRING: Enum 값의 이름을 데이터베이스에 저장합니다. 이 방법은 보다 안전하고 직관적입니다. 
//	Enum 값이 어떻게 변경되더라도, 데이터베이스에 저장된 값은 항상 올바른 Enum 값을 참조하게 됩니다.
//
//	따라서 Java 코드에서 Enum을 사용하면, 개발자가 실수로 잘못된 값을 사용하는 것을 방지하고, 데이터베이스에 저장된 값도 항상 올바른 Enum 값을 참조하게 됩니다. 
//	이는 코드의 안정성과 데이터의 무결성을 보장하는데 중요한 역할을 합니다.
}
