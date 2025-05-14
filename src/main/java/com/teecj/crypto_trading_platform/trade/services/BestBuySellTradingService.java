package com.teecj.crypto_trading_platform.trade.services;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.trade.models.BuyTradeDTO;
import com.teecj.crypto_trading_platform.trade.models.SellTradeDTO;

public interface BestBuySellTradingService {

    void performBuyTrade(BuyTradeDTO buyTradeDTO, User user);

    void performSellTrade(SellTradeDTO buyTradeDTO, User user);
}
