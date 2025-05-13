package com.teecj.crypto_trading_platform.trade.services;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;

public interface AggregatedPriceService {

    AggregatedPriceDTO getAggregatedPrice(Symbol symbol);

    boolean updateAggregatedPrice(AggregatedPriceDTO updateDTO);
}
