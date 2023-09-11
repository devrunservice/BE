package com.devrun.dto;

import java.sql.Date;

import com.devrun.entity.Couponregicode.couponstate;

public interface CouponListForStudent {
	
	int getissuedno();
	int getdiscountrate();
	String getcouponcode();
	String getlecturename();
	couponstate getstate();
	Date getexpirydate();
	Date getissueddate();

}
