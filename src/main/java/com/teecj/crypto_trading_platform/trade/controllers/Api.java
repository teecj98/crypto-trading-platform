package com.teecj.crypto_trading_platform.trade.controllers;

public class Api {
    private Api(){}

    // v1
    public static final String GET_SYMBOL_AGGREGATE_PRICE= "/api/v1/symbol/{symbol}/best-aggregated-price";
    public static final String POST_CREATE_WALLET= "/api/v1/wallet/{currency}";
    public static final String GET_USER_WALLET_BALANCE= "/api/v1/wallet/{currency}/balance";

}
