package com.devrun.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import com.devrun.dto.member.MemberDTO.Role;
import com.devrun.dto.member.MemberDTO.Status;

import lombok.Data;

@Data
@Entity
@Table(name = "api_test")
public class MemberEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userNo", length = 5)
	@Comment("유저 구분 번호")
	private int userNo;
	
	@Column(name = "name"
			, nullable = false
			, length = 20)
	@Comment("유저 이름")
	private String name;
	
	@Column(name = "email"
			, nullable = false
			, length = 50)
	@Comment("유저 이메일")
	private String email;
	
	@Column(name = "id"
			, nullable = false
			, length = 15)
	@Comment("유저 아이디")
	private String id;
	
	@Column(name = "password"
			, nullable = false
			, columnDefinition = "TEXT")
	@Comment("유저 비밀번호")
	private String password;
	
	@Column(name = "phonenumber"
			, nullable = false
			, length = 11)
	@Comment("유저 연락처")
	private String phonenumber;
	
	@Column(name = "birthday"
			, nullable = false
			)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Comment("유저 생일(생일 축하 쿠폰 발급)")
	private Date birthday;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role"
	, nullable = false
	, length = 7)
	@Comment("유저 역할 - STUDENT / MENTO / ADMIN")
	private Role role = Role.STUDENT;							// 기본값 설정
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status"
	, nullable = false
	, length = 9)
	@Comment("활성 : ACTIVE / 휴면 : INACTIVE / 탈퇴 : WITHDRAWN")
	private Status status = Status.ACTIVE;
	
	@Column(name = "signup"
			, nullable = false
			)
	@Comment("유저 가입일")
	private Date signup;
	
	@Column(name = "export", nullable = true)
	@Comment("유저 탈퇴일(탈퇴 테이블로 분리할지 고민)")
	private Date export;
	
	@Column(name = "lastlogin", nullable = true)
	@Comment("마지막 로그인 날짜 ( 휴면 전환은 서버의 온오프로 발생 )")
	private Date lastlogin;

	@Column(name = "kakaoEmailId", length = 50, nullable = true)
	@Comment("카카오 간편로그인 Email + ID")
	private String kakaoEmailId;
	
	@Column(name = "logintry", nullable = false, length = 2)
	@Comment("로그인 시도 횟수")
	private int logintry = 0;
	
	// boolean은 null값을 가질 수 없고 Boolean은 null값이 허용된다
	// 약관동의 로직이 완성되지 않은채로 테스트하려면 Boolean을 사용해야 한다
	
	@Column(name = "ageConsent", nullable = false, columnDefinition = "TINYINT(1)")
	@Comment("나이 동의")
	private Boolean ageConsent;
	
	@Column(name = "serviceConsent", nullable = false, columnDefinition = "TINYINT(1)")
	@Comment("서비스 동의")
	private Boolean termsOfService;
	
	@Column(name = "privacyConsent", nullable = false, columnDefinition = "TINYINT(1)")
	@Comment("개인정보 동의")
	private Boolean privacyConsent;
	
	@Column(name = "marketingConsent", nullable = false, columnDefinition = "TINYINT(1)")
	@Comment("광고, 마케팅 동의")
	private Boolean marketConsent;
	
	
	
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