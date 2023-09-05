package com.devrun.dto;

import java.sql.Date;

import com.devrun.entity.Couponregicode.couponstate;

public interface CouponListForMento {
	
	String gettarget();

	String getcouponcode();

	int getdiscountrate();

	int getquantity(); 

	Date getexpirydate();

	Date getissueddate();

	int getissuedno(); // issuedno는 순서에 대한 넘버링

	couponstate getstate();

}
