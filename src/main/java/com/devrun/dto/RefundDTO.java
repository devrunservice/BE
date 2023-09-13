package com.devrun.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Data
public class RefundDTO {
	
	private String merchant_uid;
	private int amount;
	private String name;
}
