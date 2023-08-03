package com.devrun.repository;

public interface PaymentInfo {
	String getBuyername();
	String getName();
	String getMerchantUid();	
	int getPaidamount();
	int getPayno();
	String getReceipturl();
	String getStatus();	
	
}
