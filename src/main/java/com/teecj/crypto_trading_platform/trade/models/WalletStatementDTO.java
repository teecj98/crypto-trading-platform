package com.teecj.crypto_trading_platform.trade.models;

import com.teecj.crypto_trading_platform.common.constant.WalletStatementType;
import com.teecj.crypto_trading_platform.trade.entities.WalletStatement;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record WalletStatementDTO(UUID uuid, UUID waletUuid, BigDecimal amount, WalletStatementType type, UUID transactionUuid, OffsetDateTime createdAt) {


    public WalletStatement toWalletStatement() {
        return new WalletStatement(uuid,waletUuid, amount, type, transactionUuid, createdAt);
    }

    public static WalletStatementDTO fromWalletStatement(WalletStatement that) {
        return new WalletStatementDTO(that.getUuid(),that.getWalletUuid(),that.getAmount(),that.getType(),that.getTransactionUuid(),that.getCreatedAt());
    }
}

