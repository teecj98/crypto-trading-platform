package com.teecj.crypto_trading_platform.trade.repositories;

import com.teecj.crypto_trading_platform.trade.entities.WalletStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WalletStatementRepository extends JpaRepository<WalletStatement, UUID> {
}
