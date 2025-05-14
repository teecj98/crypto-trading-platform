package com.teecj.crypto_trading_platform.trade.projections;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record WalletBalanceProjection(String uuid, BigDecimal balance, OffsetDateTime updatedAt) {}
