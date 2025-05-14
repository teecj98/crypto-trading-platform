package com.teecj.crypto_trading_platform.trade.entities;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.common.constant.TradeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trades")
public class Trade {
    @Id
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Symbol symbol;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private UUID transactionUuid;

}
