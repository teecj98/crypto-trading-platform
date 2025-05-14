package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.common.constant.TradeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TradeRequest(@NotNull TradeType type,
                           @NotNull Symbol symbol,
                           @DecimalMin(value="0", inclusive = false) BigDecimal amount) {
}
