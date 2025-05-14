package com.teecj.crypto_trading_platform.trade.services.impl;

import com.teecj.crypto_trading_platform.trade.entities.WalletStatement;
import com.teecj.crypto_trading_platform.trade.models.WalletStatementDTO;
import com.teecj.crypto_trading_platform.trade.repositories.WalletStatementRepository;
import com.teecj.crypto_trading_platform.trade.services.WalletStatementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WalletStatementServiceImpl implements WalletStatementService {
    private static final Logger log = LoggerFactory.getLogger(WalletStatementServiceImpl.class);

    WalletStatementRepository statementRepository;

    @Autowired
    public WalletStatementServiceImpl(WalletStatementRepository statementRepository) {
        this.statementRepository = statementRepository;
    }

    @Transactional
    @Override
    public WalletStatementDTO create(WalletStatementDTO createDTO) {
        WalletStatement saved = statementRepository.save(createDTO.toWalletStatement());
        log.info("Saved wallet statement {}", saved);

        return WalletStatementDTO.fromWalletStatement(saved);
    }
}
