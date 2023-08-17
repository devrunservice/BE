package com.devrun.entity;


import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
@Table(name = "couponissued")

public class CouponIssued {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issuedno; //쿠폰발행번호

    @Enumerated(EnumType.STRING)
    private coupontype coupontype; // 쿠폰 종류 (카테고리 할인, 특정 강의 할인 , 특정 강사 할인, 모든 강의 할인)
    private int discountrate; // 쿠폰 할인율
    private int issueduser; // 쿠폰 발급자 (외래키) 
    private Date issueddate; // 쿠폰 발행일
    private Date expirydate; // 쿠폰 일괄 만료일
    private int validityperiod; // 쿠폰 유효 기간(일단위) 생일쿠폰시,
    private int quantity; // 쿠폰 발행 수량	
    private int remove; // 삭제 여부
    private String target; // 쿠폰 적용 대상 (특정 강의 번호 또는 특정 강사 번호 또는 카테고리

    private enum coupontype{
        all,
        category,
        lecture,
        mento,

    }
}
