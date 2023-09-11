package com.devrun.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.Data;

import java.sql.Date;

@Data
@Entity
@Immutable
@Table(name= "coupon_manage")
public class CouponViewEntity {
	
	@Id	
	private Long issuedno;

    private String couponcode;

    private Integer userno;

    private int discountrate;

    private Date expirydate;

    private Date issueddate;

    private int quantity;

    private String target;
   
    @Enumerated(EnumType.STRING)
    private couponstate state; // 이 부분 추가

    public enum couponstate {
        ACTIVE, // 사용 가능 (생성 시 기본값)
        REMOVED, // 사용 정지 처리로 인한 사용 불가
        EXPIRY, // 기간 만료로 인한 사용 불가

  
    }
}
