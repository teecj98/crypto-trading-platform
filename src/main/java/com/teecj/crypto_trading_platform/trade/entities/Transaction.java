package com.teecj.crypto_trading_platform.trade.entities;

import com.teecj.crypto_trading_platform.common.constant.TransactionStatus;
import com.teecj.crypto_trading_platform.common.constant.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

}
