package com.teecj.crypto_trading_platform.trade.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {
    private final Logger log = LoggerFactory.getLogger(ErrorController.class);

    private final static String ERROR_500 = "UNKNOWN_ERROR";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[EXCEPTION ERROR] message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ERROR_500));
    }

    public record ErrorResponse(String message) {
    }
}
