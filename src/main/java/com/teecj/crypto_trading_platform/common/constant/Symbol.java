package com.teecj.crypto_trading_platform.common.constant;

public enum Symbol {
    ETHUSDT, BTCUSDT;;

    public static Currency getBaseCurrency(Symbol symbol) {
        return switch (symbol) {
            case ETHUSDT -> Currency.ETH;
            case BTCUSDT -> Currency.BTC;
        };
    }

    public static Currency getQuoteCurrency(Symbol symbol) {
        return switch (symbol) {
            case ETHUSDT, BTCUSDT -> Currency.USDT;
        };
    }
}
