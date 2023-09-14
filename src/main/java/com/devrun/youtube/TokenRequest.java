package com.devrun.youtube;

import lombok.Data;

@Data
public class TokenRequest {
	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	
}
