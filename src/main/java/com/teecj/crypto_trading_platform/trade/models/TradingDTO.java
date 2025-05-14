package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.common.constant.TradeType;

import java.math.BigDecimal;

public record TradingDTO(Symbol symbol, TradeType type, Currency deduct, BigDecimal deductAmount, Currency deposit, BigDecimal depositAmount) {
}

