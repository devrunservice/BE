package com.devrun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class PaymentDTO {
	
	public String name;
	public String buyer_email;
	public String buyer_name;
	public String buyer_tel;
	public String merchant_uid;
	public String imp_uid;
	public int paid_amount;
	public String receipt_url;
	private int mypoint;

	

}
