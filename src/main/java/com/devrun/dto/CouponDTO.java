package com.devrun.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CouponDTO {
	

    private String couponCode;
    private int lecture_price;
    private String lecture_name;
    
 
}