package com.teecj.crypto_trading_platform.trade.services.impl;

import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceDTO;
import com.teecj.crypto_trading_platform.trade.projections.WalletBalanceProjection;
import com.teecj.crypto_trading_platform.trade.repositories.WalletRepository;
import com.teecj.crypto_trading_platform.trade.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }


    @Override
    public WalletBalanceDTO findBalanceByUserId(long userId, Currency currency) {
        WalletBalanceProjection projection = walletRepository.findBalanceByUserIdAndCurrencyO(userId, currency.toString());
        if (projection == null) {
            return null;
        }
        return new WalletBalanceDTO(userId, currency, projection.balance(), projection.updatedAt());
    }
}
