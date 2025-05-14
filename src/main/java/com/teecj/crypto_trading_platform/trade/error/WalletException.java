package com.teecj.crypto_trading_platform.trade.error;

public class WalletException extends RuntimeException {

    static final String WALLET_NOT_FOUND = "WALLET_NOT_FOUND";
    static final String LOW_BALANCE = "LOW_BALANCE";
    static final String BALANCE_UPDATE_ACTION_OUTDATED = "BALANCE_UPDATE_ACTION_OUTDATED";

    public WalletException(String message) {
        super(message);
    }

    private static String getMessage(String error, String message) {
        return "[" + error + "] " + message;
    }

    public static WalletException lowBalance(String message) {
        return new WalletException(getMessage(LOW_BALANCE, message));
    }

    public static WalletException balanceUpdateOutdated(String message) {
        return new WalletException(getMessage(BALANCE_UPDATE_ACTION_OUTDATED, message));
    }

    public static WalletException walletNotFound(String message) {
        return new WalletException(getMessage(WALLET_NOT_FOUND, message));
    }
}
