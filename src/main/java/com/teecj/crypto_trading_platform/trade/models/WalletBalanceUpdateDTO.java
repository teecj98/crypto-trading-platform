package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.WalletStatementType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record WalletBalanceUpdateDTO (UUID uuid, WalletStatementType type, BigDecimal amount, OffsetDateTime lastUpdatedAt) {
}
