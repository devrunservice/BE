package com.devrun.dto;
// GlobalExceptionHandler에서 더이상 사용하지않음
// 혹시 필요할 수 있어서 남겨두지만 테스트 끝까지 사용하지 않는다면 삭제해도 무방
public class ErrorResponse {
    private String message;

    public ErrorResponse() {
    }

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}