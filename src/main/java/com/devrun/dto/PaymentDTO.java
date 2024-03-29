package com.devrun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class PaymentDTO {
	
	public String name;
	public String merchant_uid;
	public String imp_uid;
	public String couponCode;
	public int paid_amount;
	public String receipt_url;
	public int userno;
	public int mypoint;		
}
