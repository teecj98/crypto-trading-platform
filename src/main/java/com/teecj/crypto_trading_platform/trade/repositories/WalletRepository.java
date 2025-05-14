package com.teecj.crypto_trading_platform.trade.repositories;

import com.teecj.crypto_trading_platform.trade.entities.Wallet;
import com.teecj.crypto_trading_platform.trade.projections.WalletBalanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query(value = """
            SELECT balance, updated_at FROM wallets WHERE user_id = :userId AND currency = :currency
            """, nativeQuery = true
    )
    WalletBalanceProjection findBalanceByUserIdAndCurrencyO(@Param("userId") long userId, @Param("currency") String currency);
}
