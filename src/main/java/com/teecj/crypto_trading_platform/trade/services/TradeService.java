package com.teecj.crypto_trading_platform.trade.services;

import com.teecj.crypto_trading_platform.trade.models.TradingHistoryDataDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TradeService {


    List<TradingHistoryDataDTO> findTradeTransactions(long userId, Pageable pageable);
}
