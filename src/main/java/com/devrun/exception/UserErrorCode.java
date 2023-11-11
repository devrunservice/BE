package com.devrun.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    INACTIVE_USER(HttpStatus.FORBIDDEN, "User is inactive"),
    NOT_QUALIFIED(HttpStatus.UNAUTHORIZED, "User did not complete the lecture."),
    USERHASNOTLECTURE(HttpStatus.NOT_FOUND, "User has not this lecture.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}