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

    public enum LoginStatus {
        SUCCESS,
        USER_NOT_FOUND,
        PASSWORD_MISMATCH,
        ACCOUNT_INACTIVE,
        ACCOUNT_WITHDRAWN,
        LOGIN_TRIES_EXCEEDED
    }
}
