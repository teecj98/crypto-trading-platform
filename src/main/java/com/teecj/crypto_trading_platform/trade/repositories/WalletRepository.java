package com.teecj.crypto_trading_platform.trade.repositories;

import com.teecj.crypto_trading_platform.trade.entities.Wallet;
import com.teecj.crypto_trading_platform.trade.projections.WalletBalanceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    @Query(value = """
            SELECT CAST(uuid AS VARCHAR), balance, updated_at 
            FROM wallets 
            WHERE user_id = :userId AND currency = :currency
            """, nativeQuery = true
    )
    WalletBalanceProjection findBalanceByUserIdAndCurrency(@Param("userId") long userId, @Param("currency") String currency);

    @Query(value = """
            SELECT * FROM wallets WHERE user_id = :userId AND currency = :currency
            """, nativeQuery = true
    )
    Wallet findByUserIdAndCurrency(@Param("userId") long userId, @Param("currency") String currency);

    /**
     * Use of updatedAT time to avoid race condition
     * @param amount
     * @param uuid
     * @param updatedAt
     */
    @Modifying
    @Query(value = """
            UPDATE wallets 
            SET balance = balance + :amount, updated_at = :updatedAt 
            WHERE uuid = :uuid AND updated_at = :updatedAt
            """, nativeQuery = true)
    int updateBalanceByUuid(@Param("amount") BigDecimal amount, @Param("uuid") UUID uuid, @Param("updatedAt") OffsetDateTime updatedAt);
}
