package com.teecj.crypto_trading_platform.trade.entities;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="aggregated_prices")
public class AggregatedPrice {
    @Id
    @Enumerated(EnumType.STRING)
    private Symbol symbol;

    @Column(precision = 19, scale = 2)
    private BigDecimal bid;
    @Column(precision = 19, scale = 2)
    private BigDecimal  ask;

    private OffsetDateTime updatedAt;

}
