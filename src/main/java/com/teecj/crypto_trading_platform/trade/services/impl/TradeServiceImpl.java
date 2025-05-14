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
    private final TransactionRepository transactionRepository;

    private final WalletService walletService;
    private final WalletStatementService walletStatementService;

    @Autowired
    public TradeServiceImpl(TradeRepository tradeRepository, TransactionRepository transactionRepository, WalletService walletService, WalletStatementService walletStatementService) {
        this.tradeRepository = tradeRepository;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
        this.walletStatementService = walletStatementService;
    }


    @Transactional
    public void performTrade(TradingDTO tradingDTO, User user) throws WalletException {
        long traderId = user.getId();
        OffsetDateTime now = OffsetDateTime.now();

        // wallet to deduct
        WalletDTO deductWallet = walletService.findByUserIdAndCurrency(traderId, tradingDTO.deduct());
        // wallet to deposit
        WalletDTO depositWallet = walletService.findByUserIdAndCurrency(traderId, tradingDTO.deposit());


        // deduct
        // check if can deduct
        if (deductWallet == null)  {
            throw WalletException.walletNotFound("[perform trade] user has no wallet for trading deduct");
        }
        if (deductWallet.balance().compareTo(tradingDTO.deductAmount()) < 0) {
            logger.info("[Trade] wallet to deduce has balance less than amount needed {}| traderId: {} | symbol: {}",
                    tradingDTO.deductAmount(), traderId, tradingDTO.symbol());
            throw WalletException.lowBalance("[perform trade] wallet uuid " + deductWallet.uuid());
        }
        WalletBalanceUpdateDTO deductDto = new WalletBalanceUpdateDTO(deductWallet.uuid(), WalletStatementType.OUT, tradingDTO.deductAmount(), deductWallet.updatedAt());
        walletService.updateWalletBalance(deductDto);

        // deposit
        if (depositWallet == null)  {
            throw WalletException.walletNotFound("[perform trade] user has no wallet for trading deposit");
        }
        WalletBalanceUpdateDTO depositDto = new WalletBalanceUpdateDTO(depositWallet.uuid(), WalletStatementType.IN, tradingDTO.depositAmount(), depositWallet.updatedAt());
        walletService.updateWalletBalance(depositDto);


        // add transaction records
        Transaction transaction = new Transaction(UUID.randomUUID(), TransactionType.TRADE, TransactionStatus.FULFILLED, traderId, now);
        transactionRepository.save(transaction);

        BigDecimal tradeAmount = tradingDTO.type() == TradeType.BUY ? tradingDTO.depositAmount() : tradingDTO.deductAmount();
        Trade trade = new Trade(UUID.randomUUID(), tradingDTO.type(), tradingDTO.symbol(), tradeAmount, transaction.getUuid());
        tradeRepository.save(trade);

        // add wallet statements
        walletStatementService.create(new WalletStatementDTO(UUID.randomUUID(), deductWallet.uuid(), tradingDTO.deductAmount(), WalletStatementType.OUT, transaction.getUuid(), now));
        walletStatementService.create(new WalletStatementDTO(UUID.randomUUID(), depositWallet.uuid(), tradingDTO.depositAmount(), WalletStatementType.IN, transaction.getUuid(), now));

        logger.info("[Trade] Successfully performed trade {}", tradingDTO);
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
