package com.devrun.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CouponResponseDTO {
	
	private List<Integer> prices;
	private List<Integer> discountprice;
	
	public void setPrices(List<Integer> prices) {
		this.prices = prices;
	}
	
	public void setDiscountprice(List<Integer> discountprice) {
		this.discountprice = discountprice;
	}
	
}
