package com.devrun.dto;
import lombok.NoArgsConstructor;
import lombok.ToString;


@ToString
@NoArgsConstructor
public class CouponDTO {
	

    private String CouponCode;
    private int Amount;
    
    public String getCouponCode() {
        return CouponCode;
    }

    public void setCouponCode(String couponCode) {
    	CouponCode = couponCode;
    }
    
    public int getamount() {
        return Amount;
    }

    public void setAmount(int amount) {
    	Amount = amount;
    }
    
 
}