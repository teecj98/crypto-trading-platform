package com.teecj.crypto_trading_platform.trade.controllers;

import com.teecj.crypto_trading_platform.trade.error.ErrorCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorController {
    private final Logger log = LoggerFactory.getLogger(ErrorController.class);

    private final static String GENERIC_ERROR = "GENERIC_ERROR";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("[EXCEPTION ERROR] message: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(GENERIC_ERROR);
    }

    @ExceptionHandler(ErrorCodeException.class)
    public ResponseEntity<String> handleErrorCodeException(ErrorCodeException e) {
        log.error("[Eror COde EXCEPTION] code: {} | message: {}", e.getCode(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getCode());
    }

}
