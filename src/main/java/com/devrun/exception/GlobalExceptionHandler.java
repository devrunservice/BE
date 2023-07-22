package com.devrun.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devrun.dto.ErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> customValidationErrorHandling(MethodArgumentNotValidException exception) {
        // 첫 번째 에러의 defaultMessage를 가져옵니다.
        String errorMessage = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();

//        // 새로운 오류 응답 객체를 만듭니다.
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setMessage(errorMessage);

//        // ErrorResponse 객체를 포함하는 ResponseEntity를 반환합니다.
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}