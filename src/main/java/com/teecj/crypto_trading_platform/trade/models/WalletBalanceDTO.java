package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Currency;

import java.math.BigDecimal;
import java.util.UUID;


public record WalletBalanceDTO(UUID uuid, long userId, Currency currency, BigDecimal balance) {

}
