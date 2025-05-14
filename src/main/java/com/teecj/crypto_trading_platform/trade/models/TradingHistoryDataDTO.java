package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.common.constant.TradeType;
import com.teecj.crypto_trading_platform.common.constant.TransactionStatus;
import com.teecj.crypto_trading_platform.trade.entities.Trade;
import com.teecj.crypto_trading_platform.trade.entities.Transaction;
import com.teecj.crypto_trading_platform.trade.projections.TradeTransactionProjection;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TradingHistoryDataDTO(UUID tradeUuid, TradeType tradeType, Symbol symbol, BigDecimal amount,
                                    TransactionStatus status, long traderId, OffsetDateTime createdAt) {

public static TradingHistoryDataDTO fromTrade(TradeTransactionProjection projection) {
    Trade trade = projection.trade();
    Transaction transaction = projection.transaction();
    return new TradingHistoryDataDTO(trade.getUuid(), trade.getType(), trade.getSymbol(), trade.getAmount(),
            transaction.getStatus(), transaction.getUserId(), transaction.getCreatedAt());
}
}
