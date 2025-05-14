package com.teecj.crypto_trading_platform.trade.services.impl;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.common.constant.TradeType;
import com.teecj.crypto_trading_platform.trade.error.TradingException;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import com.teecj.crypto_trading_platform.trade.models.BuyTradeDTO;
import com.teecj.crypto_trading_platform.trade.models.SellTradeDTO;
import com.teecj.crypto_trading_platform.trade.models.TradingDTO;
import com.teecj.crypto_trading_platform.trade.services.AggregatedPriceService;
import com.teecj.crypto_trading_platform.trade.services.BestBuySellTradingService;
import com.teecj.crypto_trading_platform.trade.services.TradeService;
import com.teecj.crypto_trading_platform.trade.utils.PriceCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BestBuySellServiceImpl implements BestBuySellTradingService {

    private static final Logger logger = LoggerFactory.getLogger(BestBuySellServiceImpl.class);

    private final AggregatedPriceService aggregatedPriceService;
    private final TradeService tradeService;

    @Autowired
    public BestBuySellServiceImpl(AggregatedPriceService aggregatedPriceService, TradeService tradeService) {
        this.aggregatedPriceService = aggregatedPriceService;
        this.tradeService = tradeService;
    }

    @Transactional
    @Override
    public void performBuyTrade(BuyTradeDTO buyTradeDTO, User user) {
        // find the latest best price to buy
        AggregatedPriceDTO aggregatedPrice = aggregatedPriceService.getAggregatedPrice(buyTradeDTO.symbol());
        BigDecimal bestAskPrice = aggregatedPrice.ask();
        if (bestAskPrice.compareTo(BigDecimal.ZERO) == 0) {
            throw TradingException.invalidAggregatedPrice("[buy trade] zero best ask price: " + buyTradeDTO.symbol());
        }

        BigDecimal buyPrice = PriceCalculator.calculateTradePrice(bestAskPrice, buyTradeDTO.amount());
        logger.info("[buy trade] buy amount: {} | best ask price: {} | buyPrice {}", buyTradeDTO.amount(), bestAskPrice, buyPrice);
        if (buyPrice.compareTo(BigDecimal.TEN) < 0) {
            throw TradingException.invalidTradeAmount("buy price too low: " + buyPrice);
        }

        // base wallet to deposit
        Currency base = Symbol.getBaseCurrency(buyTradeDTO.symbol());
        // quote wallet to deduct
        Currency quote = Symbol.getQuoteCurrency(buyTradeDTO.symbol());

        // do trading
        TradingDTO tradingDTO = new TradingDTO(buyTradeDTO.symbol(), TradeType.BUY, quote, buyPrice, base, buyTradeDTO.amount());
        tradeService.performTrade(tradingDTO, user);
    }

    @Transactional
    @Override
    public void performSellTrade(SellTradeDTO sellTradeDTO, User user) {
        // find the latest best price
        AggregatedPriceDTO aggregatedPrice = aggregatedPriceService.getAggregatedPrice(sellTradeDTO.symbol());
        BigDecimal bestBidPrice = aggregatedPrice.bid();
        if (bestBidPrice.compareTo(BigDecimal.ZERO) == 0) {
            throw TradingException.invalidAggregatedPrice("[buy trade] zero best ask price: " + sellTradeDTO.symbol());
        }
        // calculate sell price
        BigDecimal sellPrice = PriceCalculator.calculateTradePrice(bestBidPrice, sellTradeDTO.amount());
        logger.info("[sell trade] sell amount: {} | best bid price: {} | sellPrice {}", sellTradeDTO.amount(), bestBidPrice, sellPrice);
        if (sellPrice.compareTo(BigDecimal.TEN) < 0) {
            throw TradingException.invalidTradeAmount("sell price too low: " + sellPrice);
        }
        // base currency to deduct
        Currency base = Symbol.getBaseCurrency(sellTradeDTO.symbol());
        // quote currency to deposit
        Currency quote = Symbol.getQuoteCurrency(sellTradeDTO.symbol());

        // do trading
        TradingDTO tradingDTO = new TradingDTO(sellTradeDTO.symbol(), TradeType.SELL, base, sellTradeDTO.amount(), quote, sellPrice);
        tradeService.performTrade(tradingDTO, user);
    }

}
