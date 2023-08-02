package com.devrun.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devrun.exception.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<?> customValidationErrorHandling(MethodArgumentNotValidException exception) {
//        // 첫 번째 에러의 defaultMessage를 가져옵니다.
//        String errorMessage = exception.getBindingResult().getAllErrors().get(0).getDefaultMessage();
//
////        // 새로운 오류 응답 객체를 만듭니다.
////        ErrorResponse errorResponse = new ErrorResponse();
////        errorResponse.setMessage(errorMessage);
//
////        // ErrorResponse 객체를 포함하는 ResponseEntity를 반환합니다.
//        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity<Object> handleQuizException(final RestApiException e) {
        final ErrorCode errorCode = e.getErrorCode();
        return handleExceptionInternal(errorCode);
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> nullex(final NullPointerException e) {

        return ResponseEntity.badRequest().body("Can't Find Data, Check Your Request -By DevRun");
    }


}