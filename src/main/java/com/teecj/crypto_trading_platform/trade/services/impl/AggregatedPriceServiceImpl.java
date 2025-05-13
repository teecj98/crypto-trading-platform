package com.teecj.crypto_trading_platform.trade.services.impl;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.entities.AggregatedPrice;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import com.teecj.crypto_trading_platform.trade.repositories.AggregatedPriceRepository;
import com.teecj.crypto_trading_platform.trade.services.AggregatedPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AggregatedPriceServiceImpl implements AggregatedPriceService {
    private static final Logger logger = LoggerFactory.getLogger(AggregatedPriceServiceImpl.class);

    private final AggregatedPriceRepository aggregatedPriceRepository;

    @Autowired
    public AggregatedPriceServiceImpl(AggregatedPriceRepository aggregatedPriceRepository) {
        this.aggregatedPriceRepository = aggregatedPriceRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames="aggregated-price", key="#symbol")
    @Override
    public AggregatedPriceDTO getAggregatedPrice(Symbol symbol) {
        Optional<AggregatedPrice> optional = aggregatedPriceRepository.findById(symbol);
        return optional.map(AggregatedPriceDTO::fromAggregatedPrice).orElse(null);
    }

    @Transactional
    @CacheEvict(cacheNames="aggregated-price", key="#updateDTO.symbol()")
    @Override
    public boolean updateAggregatedPrice(AggregatedPriceDTO updateDTO) {
        int updatedNum = aggregatedPriceRepository.updateAggregatedPrice(updateDTO.bid(), updateDTO.ask(), updateDTO.updatedAt(), updateDTO.symbol().toString());
        boolean updated = updatedNum != 0;
        logger.info("[AggregatedPrice update] updated:{} | {}",  updated, updateDTO);
        return updated;
    }

}
