package com.teecj.crypto_trading_platform.trade.projections;

import com.teecj.crypto_trading_platform.trade.entities.Trade;
import com.teecj.crypto_trading_platform.trade.entities.Transaction;

public record TradeTransactionProjection(Trade trade, Transaction transaction) {
}

