package com.teecj.crypto_trading_platform.trade.repositories;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.entities.AggregatedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Repository
public interface AggregatedPriceRepository extends JpaRepository<AggregatedPrice, Symbol> {

    @Transactional
    @Modifying
    @Query(value = """
            UPDATE aggregated_prices SET bid=:bid, ask=:ask, updated_at=:updatedAt 
            WHERE symbol=:symbol 
            """, nativeQuery = true)
    int updateAggregatedPrice(@Param("bid") BigDecimal bid, @Param("ask") BigDecimal ask, @Param("updatedAt") OffsetDateTime updatedAt, @Param("symbol") String symbol);
}
