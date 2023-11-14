package com.devrun.dto;

import java.util.Date;

import com.devrun.entity.Couponregicode.couponstate;

public interface CouponListInCart {
	Long getlectureid();
	String getlecturename();
	String getCouponcode();
	int getdiscountrate();
	Date getexpirydate();
	couponstate getstate();
}
