package com.teecj.crypto_trading_platform.trade.repositories;

import com.teecj.crypto_trading_platform.trade.entities.Trade;
import com.teecj.crypto_trading_platform.trade.projections.TradeTransactionProjection;
import org.hibernate.boot.model.source.spi.Sortable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TradeRepository extends JpaRepository<Trade, UUID> {

    @Query(value = """
            SELECT new com.teecj.crypto_trading_platform.trade.projections.TradeTransactionProjection(td, tx)
            FROM Trade td JOIN Transaction tx ON tx.uuid = td.transactionUuid
            WHERE tx.userId = :userId
            order by tx.createdAt ASC
            """)
    List<TradeTransactionProjection> findTradeTransactionsOrderedByCreatedDate(@Param("userId") long userId, Pageable pageable);

}
