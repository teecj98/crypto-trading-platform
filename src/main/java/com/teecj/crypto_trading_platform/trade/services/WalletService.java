package com.teecj.crypto_trading_platform.trade.services;

import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceDTO;
import com.teecj.crypto_trading_platform.trade.projections.WalletBalanceProjection;

public interface WalletService {

    WalletBalanceDTO findBalanceByUserId(long userId, Currency currency);
}
