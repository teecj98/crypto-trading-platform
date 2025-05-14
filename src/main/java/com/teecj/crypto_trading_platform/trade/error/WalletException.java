package com.teecj.crypto_trading_platform.trade.error;

public class WalletException extends RuntimeException {


    static final String BALANCE_UPDATE_ACTION_OUTDATED = "BALANCE_UPDATE_ACTION_OUTDATED";

    public WalletException(String message) {
        super(message);
    }

    private static String getMessage(String error, String message) {
        return "[" + error + "] " + message;
    }


    public static WalletException balanceUpdateOutdated(String message) {
        return new WalletException(getMessage(BALANCE_UPDATE_ACTION_OUTDATED, message));
    }


}
