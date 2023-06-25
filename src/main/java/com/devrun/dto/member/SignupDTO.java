package com.devrun.dto.member;

import lombok.Data;

@Data
public class SignupDTO {
	
	private String id, email, phonenumber;
	
	private int code;
	
}
