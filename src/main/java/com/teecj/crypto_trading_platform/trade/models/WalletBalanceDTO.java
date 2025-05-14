package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Currency;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


public record WalletBalanceDTO(long userId, Currency currency, BigDecimal balance, OffsetDateTime updatedAt) {
}
