package com.devrun.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;

import lombok.Data;

@Data
@Entity
public class Couponregicode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponcodeno;
    
    private String couponcode; // 쿠폰 등록 코드 - 자동 생성됨
    
    @ManyToOne
    @JoinColumn(name = "issuedno")
    private CouponIssued issuedno; //쿠폰발행번호 - 외래키
    
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    private couponstate state; // 쿠폰 상태
    private enum couponstate{
        ACTIVE, // 사용 가능 (생성 시 기본값)
        REMOVED, // 사용 정지 처리로 인한 사용 불가
        EXPIRY, // 기간 만료로 인한 사용 불가
    }
}
