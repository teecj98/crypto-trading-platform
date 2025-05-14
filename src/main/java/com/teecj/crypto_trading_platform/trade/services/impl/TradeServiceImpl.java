package com.teecj.crypto_trading_platform.trade.services.impl;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.common.constant.TradeType;
import com.teecj.crypto_trading_platform.common.constant.TransactionStatus;
import com.teecj.crypto_trading_platform.common.constant.TransactionType;
import com.teecj.crypto_trading_platform.common.constant.WalletStatementType;
import com.teecj.crypto_trading_platform.trade.entities.Trade;
import com.teecj.crypto_trading_platform.trade.entities.Transaction;
import com.teecj.crypto_trading_platform.trade.error.WalletException;
import com.teecj.crypto_trading_platform.trade.models.*;
import com.teecj.crypto_trading_platform.trade.projections.TradeTransactionProjection;
import com.teecj.crypto_trading_platform.trade.repositories.TradeRepository;
import com.teecj.crypto_trading_platform.trade.repositories.TransactionRepository;
import com.teecj.crypto_trading_platform.trade.services.TradeService;
import com.teecj.crypto_trading_platform.trade.services.WalletService;
import com.teecj.crypto_trading_platform.trade.services.WalletStatementService;
import org.hibernate.boot.model.source.spi.Sortable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TradeServiceImpl implements TradeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);

    private final TradeRepository tradeRepository;

    @Autowired
    public TradeServiceImpl(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;

    }


    @Transactional(readOnly = true)
    @Override
    public List<TradingHistoryDataDTO> findTradeTransactions(long userId, Pageable pageable) {

        List<TradeTransactionProjection> projections = tradeRepository.findTradeTransactionsOrderedByCreatedDate(userId, pageable);
        if (projections == null || projections.isEmpty()) {
            return List.of();
        }

        List<TradingHistoryDataDTO> dtos = new ArrayList<>(projections.size());
        for (TradeTransactionProjection projection : projections) {
            dtos.add(TradingHistoryDataDTO.fromTrade(projection));
        }

        return dtos;
    }
}
