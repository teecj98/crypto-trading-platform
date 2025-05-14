package com.teecj.crypto_trading_platform.trade.entities;

import com.teecj.crypto_trading_platform.common.constant.WalletStatementType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wallet_statements")
public class WalletStatement {
    @Id
    private UUID uuid;

    @Column(nullable = false)
    private UUID walletUuid;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletStatementType type;

    @Column(nullable = false)
    private UUID transactionUuid;

    @Column(nullable = false)
    private OffsetDateTime createdAt;
}
