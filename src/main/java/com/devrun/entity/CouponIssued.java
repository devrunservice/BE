package com.devrun.entity;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.devrun.youtube.Lecture;

import lombok.Data;

@Data
@Entity
@Table(name = "couponissued")
public class CouponIssued {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long issuedno; // 쿠폰발행번호
	@Enumerated(EnumType.STRING)
	private coupontype coupontype; // 쿠폰 종류 (카테고리 할인, 특정 강의 할인 , 특정 강사 할인, 모든 강의 할인)
	@Max(value = 99)
	@Min(value = 1)
	private int discountrate; // 쿠폰 할인율
	@ManyToOne
	@JoinColumn(name = "issueduser")
	private MemberEntity issueduser; // 쿠폰 발급자 - 멘토,강사 (외래키)
	private Date issueddate; // 쿠폰 발행일
	private Date expirydate; // 쿠폰 일괄 만료일
	private int validityperiod; // 쿠폰 유효 기간(일단위) 생일쿠폰?
	@Max(value = 100)
	@Min(value = 1)
	private int quantity; // 쿠폰 발행 수량
	
	@ManyToOne
	@JoinColumn(name = "lectureid")
	private Lecture lectureid; // 쿠폰 적용 대상 (특정 강의 번호 또는 특정 강사 번호 또는 카테고리)

	private enum coupontype {
		all, category, lecture, mento,

	}

}
