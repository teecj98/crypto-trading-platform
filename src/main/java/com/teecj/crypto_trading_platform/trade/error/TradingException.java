package com.teecj.crypto_trading_platform.trade.error;

public class TradingException extends ErrorCodeException {

    static final String INVALID_AGGREGATED_PRICE = "INVALID_AGGREGATED_PRICE";
    static final String INVALID_TRADE_AMOUNT = "INVALID_TRADE_AMOUNT";

    public TradingException(String code, String message) {
        super(code, message);
    }

    private static String getMessage(String error, String message) {
        return "[" + error + "] " + message;
    }

    public static TradingException invalidAggregatedPrice(String message) {
        return new TradingException(INVALID_AGGREGATED_PRICE, getMessage(INVALID_AGGREGATED_PRICE, message));
    }

    public static TradingException invalidTradeAmount(String message) {
        return new TradingException(INVALID_TRADE_AMOUNT, getMessage(INVALID_TRADE_AMOUNT, message));
    }

}
