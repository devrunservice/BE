package com.devrun.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginDTO {

    private LoginStatus status;
    private String message;
    private String username;
    private String Authorization;
    
    // Status와 Message만 받는 생성자
    public LoginDTO(LoginStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // Status, Message, Username, Token을 받는 생성자
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