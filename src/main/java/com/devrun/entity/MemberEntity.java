package com.devrun.entity;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Comment;
import org.springframework.format.annotation.DateTimeFormat;

import com.devrun.dto.member.MemberDTO.Role;
import com.devrun.dto.member.MemberDTO.Status;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class MemberEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "userNo", length = 5)
	@Comment("유저 구분 번호")
	private int userNo;
	
	@Column(name = "name"
			, nullable = false												// 모든 컨트롤러에서 @Valid로 검증하는 것이 아니기 때문에 사용할지 말지일단 Keep
			, length = 20)
	@Comment("유저 이름")
	@NotBlank(message = "information cannot be null or empty")
	private String name;
	
	@Column(name = "email"
			, nullable = false
			, length = 50)
	@Comment("유저 이메일")
	@NotBlank(message = "information cannot be null or empty")
	private String email;
	
	@Column(name = "id"
			, nullable = false
			, length = 15)
	@Comment("유저 아이디")
	@NotBlank(message = "information cannot be null or empty")
	private String id;
	
	@Column(name = "password"
			, nullable = false
			, columnDefinition = "TEXT")
	@Comment("유저 비밀번호")
	@NotBlank(message = "information cannot be null or empty")
	private String password;
	
	@Column(name = "phonenumber"
			, nullable = false
			, length = 11)
	@Comment("유저 연락처")
	@NotBlank(message = "information cannot be null or empty")
	private String phonenumber;
	
	@Column(name = "birthday"
			, nullable = false
			)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Comment("유저 생일(생일 축하 쿠폰 발급)")
	@NotNull(message = "information cannot be null or empty")
	private LocalDate birthday;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role"
	, nullable = false
	, length = 7)
	@Comment("유저 역할 - STUDENT / MENTO / ADMIN")
	private Role role = Role.STUDENT;										// 기본값 설정
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status"
	, nullable = false
	, length = 9)
	@Comment("활성 : ACTIVE / 휴면 : INACTIVE / 탈퇴 : WITHDRAWN")
	private Status status = Status.INACTIVE;
	
	@Column(name = "signup"
			, nullable = false
			)
	@Comment("유저 가입일")
	private Date signupDate;
	
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

    @Column(name = "ageConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("나이 동의")
    @AssertTrue(message = "User has not agreed to the terms")
    private boolean ageConsent;

    @Column(name = "serviceConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("서비스 동의")
    @AssertTrue(message = "User has not agreed to the terms")
    private boolean termsOfService;

    @Column(name = "privacyConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("개인정보 동의")
    @AssertTrue(message = "User has not agreed to the terms")
    private boolean privacyConsent;

    @Column(name = "marketingConsent", nullable = false, columnDefinition = "TINYINT(1)")
    @Comment("광고, 마케팅 동의")
    private boolean marketConsent;

    @Column(name = "profileimgsrc")
    @NotBlank
    @Comment("유저 프로필 이미지 주소")
    private String profileimgsrc = "profile.png";


    // OneToOne 어노테이션을 사용하여 1:1 관계 설정
//    @OneToOne(mappedBy = "memberEntity")
//    private PointEntity pointEntity;
    
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
//    
//	1. 가독성
//	데이터베이스에서 enum 값을 확인할 때 가독성을 높여준다.
//	예를 들어, "STANDBY", "ING", "PAUSE"와 같은 문자열 값을 확인하면 어떤 상태인지 쉽게 이해할 수 있다.
//
//	2. 변경 내역의 영향을 줄임
//	enum 상수의 순서가 변경되거나 새로운 상수가 추가되어도 데이터베이스에 저장된 값에는 영향을 주지 않는다.
//	엔티티 클래스의 코드 변경 없이도 enum을 확장하고 유지보수할 수 있는 장점이 있다.
//
//	3. 확장성
//	enum 상수의 이름을 변경하거나 다국어 지원을 위해 각 상수에 대한 다른 문자열 값을 사용하는 등의 확장이 가능하다.
//	데이터베이스에는 해당 문자열 값이 저장되기 때문에, 어플리케이션에서는 enum 값을 사용할 때 변화가 없으며 데이터베이스와의 호환성을 유지할 수 있다.
//
//	4. 독립성
//	데이터베이스 시스템이 enum 자료형을 지원하지 않아도 독립적으로 동작할 수 있다.
//	다른 데이터베이스로의 전환 또는 다른 프로젝트와의 통합 시에도 enum을 문자열로 저장하여 호환성을 유지할 수 있다.
}