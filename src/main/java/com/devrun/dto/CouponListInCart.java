package com.devrun.dto;

import java.sql.Date;

import com.devrun.entity.Couponregicode.couponstate;

public interface CouponListInCart {
	String getlecturename();
	int getdiscountrate();
	Date getexpirydate();
	couponstate getstate();
}
