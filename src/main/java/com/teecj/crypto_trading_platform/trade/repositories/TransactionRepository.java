package com.teecj.crypto_trading_platform.trade.repositories;

import com.teecj.crypto_trading_platform.trade.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
