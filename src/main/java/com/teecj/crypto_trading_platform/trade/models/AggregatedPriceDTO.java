package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.entities.AggregatedPrice;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AggregatedPriceDTO(Symbol symbol, BigDecimal bid, BigDecimal ask,
                                 OffsetDateTime updatedAt) {

    public static AggregatedPriceDTO fromAggregatedPrice(AggregatedPrice that) {
        return new AggregatedPriceDTO(that.getSymbol(), that.getBid(), that.getAsk(), that.getUpdatedAt());
    }
}
