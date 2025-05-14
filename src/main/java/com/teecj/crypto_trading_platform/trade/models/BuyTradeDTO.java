package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Symbol;

import java.math.BigDecimal;

public record BuyTradeDTO(Symbol symbol, BigDecimal amount) {
}
