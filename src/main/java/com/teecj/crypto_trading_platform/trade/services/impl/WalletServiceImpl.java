package com.teecj.crypto_trading_platform.trade.services.impl;

import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.trade.entities.Wallet;
import com.teecj.crypto_trading_platform.trade.error.WalletException;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceDTO;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceUpdateDTO;
import com.teecj.crypto_trading_platform.trade.models.WalletDTO;
import com.teecj.crypto_trading_platform.trade.projections.WalletBalanceProjection;
import com.teecj.crypto_trading_platform.trade.repositories.WalletRepository;
import com.teecj.crypto_trading_platform.trade.services.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.teecj.crypto_trading_platform.common.constant.WalletStatementType.OUT;

@Service
public class WalletServiceImpl implements WalletService {
    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;

    @Autowired
    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    @Override
    public WalletDTO createNewWallet(long userId, Currency currency) {
        OffsetDateTime now = OffsetDateTime.now();
        Wallet wallet = new Wallet(UUID.randomUUID(), userId, currency, BigDecimal.ZERO, now, now);
        Wallet saved = walletRepository.save(wallet);
        walletRepository.flush();
        logger.info("[Created new wallet]: {}", wallet);
        return WalletDTO.fromWallet(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public WalletDTO findByUserIdAndCurrency(long userId, Currency currency) {
        Wallet wallet = walletRepository.findByUserIdAndCurrency(userId, currency.toString());
        if (wallet == null) {
            return null;
        }
        return WalletDTO.fromWallet(wallet);
    }

    @Transactional(readOnly = true)
    @Override
    public WalletBalanceDTO findBalanceByUserIdAndCurrency(long userId, Currency currency) {
        WalletBalanceProjection projection = walletRepository.findBalanceByUserIdAndCurrency(userId, currency.toString());
        if (projection == null) {
            return null;
        }
        return new WalletBalanceDTO(UUID.fromString(projection.uuid()), userId, currency, projection.balance());
    }


    @Transactional
    @Override
    public void updateWalletBalance(WalletBalanceUpdateDTO updateDTO) {
        BigDecimal amount = updateDTO.type() == OUT ? updateDTO.amount().negate() : updateDTO.amount();
        int updatedNum = walletRepository.updateBalanceByUuid(amount, updateDTO.uuid(), updateDTO.lastUpdatedAt());
        if (updatedNum == 0) {
            throw WalletException.balanceUpdateOutdated("[update wallet balance] wallet uuid:" + updateDTO.uuid());
        }

        logger.info("[update wallet balance] updated successfully | {}", updateDTO);
    }
}
