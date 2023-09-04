package com.devrun.entity;

import javax.persistence.Entity;
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

    private int userno;

    private int discountrate;

    private Date expirydate;

    private Date issueddate;

    private int quantity;

    private String target;

    private String state;

}
