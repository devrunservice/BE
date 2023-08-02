package com.devrun.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException{

    private final ErrorCode errorCode;

}
