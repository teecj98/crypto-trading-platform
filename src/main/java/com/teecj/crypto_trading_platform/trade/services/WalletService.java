package com.teecj.crypto_trading_platform.trade.services;

import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceDTO;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceUpdateDTO;
import com.teecj.crypto_trading_platform.trade.models.WalletDTO;

public interface WalletService {

    WalletDTO createNewWallet(long userId, Currency currency);

    WalletDTO findByUserIdAndCurrency(long userId, Currency currency);

    WalletBalanceDTO findBalanceByUserIdAndCurrency(long userId, Currency currency);

    void updateWalletBalance(WalletBalanceUpdateDTO updateDTO);
}
