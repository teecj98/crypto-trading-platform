package com.teecj.crypto_trading_platform.trade.error;

import lombok.Getter;

@Getter
public abstract class ErrorCodeException extends RuntimeException {

    private final String code;

    public ErrorCodeException(String code, String message) {
        super(message);
        this.code = code;
    }
}
