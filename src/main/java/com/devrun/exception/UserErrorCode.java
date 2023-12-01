package com.devrun.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    INACTIVE_USER(HttpStatus.FORBIDDEN, "User is inactive"),
    NOT_QUALIFIED(HttpStatus.UNAUTHORIZED, "User did not complete the lecture."),
    USERHASNOTLECTURE(HttpStatus.OK, "User has not this lecture."),
    POSSESSION(HttpStatus.OK, "This Resource is not this user's")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}