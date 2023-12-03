package com.devrun.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

	// null값인경우 response에 포함시키지 않는다
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private LoginStatus status;
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String phonenumber;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("Access_token")
    private String access_token;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("Refresh_token")
    private String refresh_token;
    
    // status와 message만 받는 생성자
    public LoginDTO(LoginStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // status, message, username 을 받는 생성자
    public LoginDTO(LoginStatus status, String message, String username) {
        this.status = status;
        this.message = message;
        this.username = username;
    }
    
    public enum LoginStatus {
        SUCCESS,
        USER_NOT_FOUND,
        PASSWORD_MISMATCH,
        ACCOUNT_INACTIVE,
        ACCOUNT_WITHDRAWN,
        LOGIN_TRIES_EXCEEDED
    }
}