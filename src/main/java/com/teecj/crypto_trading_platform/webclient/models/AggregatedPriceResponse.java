package com.teecj.crypto_trading_platform.webclient.models;

import com.teecj.crypto_trading_platform.common.constant.Symbol;

import java.math.BigDecimal;

public record AggregatedPriceResponse(Symbol symbol, BigDecimal bidPrice, BigDecimal askPrice) {
}
