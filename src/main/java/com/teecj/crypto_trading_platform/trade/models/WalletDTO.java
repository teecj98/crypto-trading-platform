package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.trade.entities.Wallet;
import com.teecj.crypto_trading_platform.trade.error.WalletException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


public record WalletDTO(UUID uuid, long userId, Currency currency, BigDecimal balance, OffsetDateTime createdAt,
                        OffsetDateTime updatedAt, long version) {

    public static WalletDTO fromWallet(Wallet wallet) throws WalletException {
        return new WalletDTO(wallet.getUuid(), wallet.getUserId(), wallet.getCurrency(), wallet.getBalance(), wallet.getCreatedAt(), wallet.getUpdatedAt(), wallet.getVersion());
    }
}
