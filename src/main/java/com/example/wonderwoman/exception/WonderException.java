package com.example.wonderwoman.exception;

import lombok.Getter;

@Getter
public class WonderException extends RuntimeException {
    private int status;
    private String message;
    private String solution;

    public WonderException(ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.status = errorCode.getHttpStatus().value();
        this.solution = errorCode.getSolution();
    }

    public WonderException(ErrorCode errorCode, String message) {
        this.message = message;
        this.status = errorCode.getHttpStatus().value();
        this.solution = errorCode.getSolution();
    }

    public WonderException(ErrorCode errorCode, String message, String solution) {
        this.message = message;
        this.status = errorCode.getHttpStatus().value();
        this.solution = solution;
    }
}
