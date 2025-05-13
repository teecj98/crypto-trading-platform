package com.teecj.crypto_trading_platform.trade.scheduler;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import com.teecj.crypto_trading_platform.trade.services.AggregatedPriceService;
import com.teecj.crypto_trading_platform.trade.utils.PriceCalculator;
import com.teecj.crypto_trading_platform.webclient.CryptoPlatformWebClient;
import com.teecj.crypto_trading_platform.webclient.impl.BinanceWebClient;
import com.teecj.crypto_trading_platform.webclient.impl.HuobiWebClient;
import com.teecj.crypto_trading_platform.webclient.models.AggregatedPriceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class AggregatedPriceScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AggregatedPriceScheduler.class);


    private final AggregatedPriceService aggregatedPriceService;

    private final CryptoPlatformWebClient binanceWebClient;
    private final CryptoPlatformWebClient huobiWebClient;

    @Autowired
    public AggregatedPriceScheduler(AggregatedPriceService aggregatedPriceService, BinanceWebClient binanceWebClient, HuobiWebClient huobiWebClient) {
        this.aggregatedPriceService = aggregatedPriceService;
        this.binanceWebClient = binanceWebClient;
        this.huobiWebClient = huobiWebClient;
    }


    @Scheduled(timeUnit = TimeUnit.MILLISECONDS, fixedRateString = "${scheduler.aggregated.price.time.ms}")
    public void updateAggregatedPriceOfETHUSDT() {
        updateAggregatedPrice(Symbol.ETHUSDT);
    }

    @Scheduled(timeUnit = TimeUnit.MILLISECONDS, fixedRateString = "${scheduler.aggregated.price.time.ms}")
    public void updateAggregatedPriceOfBTCUSDT() {
        updateAggregatedPrice(Symbol.BTCUSDT);

    }


    protected void updateAggregatedPrice(Symbol symbol) {
        try {
            LocalDateTime start = LocalDateTime.now();
            logger.info("[{} Scheduled update] Start: {}", symbol, start);

            // non-blocking requests
            Mono<AggregatedPriceResponse> binanceMono = binanceWebClient.getAggregatedPrice(symbol);
            Mono<AggregatedPriceResponse> huobiMono = huobiWebClient.getAggregatedPrice(symbol);

            // await all
            AggregatedPriceResponse binanceResponse = binanceMono.block();
            AggregatedPriceResponse huobiResponse = huobiMono.block();

            logger.info("[{} Scheduled update] Binance: {}", symbol, binanceResponse);
            logger.info("[{} Scheduled update] Huobi: {}", symbol, huobiResponse);

            if (binanceResponse == null && huobiResponse == null) {
                logger.info("[{} Scheduled update] No aggregated price for Binance and Huobi, skip. {}", symbol, LocalDateTime.now());
                return;
            }

            // start update
            OffsetDateTime now = OffsetDateTime.now();
            if (binanceResponse != null && huobiResponse != null) {
                // compare and update
                BigDecimal bestBid = PriceCalculator.findBestBid(binanceResponse.bidPrice(), huobiResponse.bidPrice());
                BigDecimal bestAsk = PriceCalculator.findBestAsk(binanceResponse.askPrice(), huobiResponse.askPrice());
                AggregatedPriceDTO dto = new AggregatedPriceDTO(symbol, bestBid, bestAsk, now);
                aggregatedPriceService.updateAggregatedPrice(dto);
            } else if (binanceResponse != null) {
                // missing huobi response, update only using binance response
                AggregatedPriceDTO dto = new AggregatedPriceDTO(symbol, binanceResponse.bidPrice(), binanceResponse.askPrice(), now);
                aggregatedPriceService.updateAggregatedPrice(dto);
            } else {
                // missing binance response, update only using huobi response
                AggregatedPriceDTO dto = new AggregatedPriceDTO(symbol, huobiResponse.bidPrice(), huobiResponse.askPrice(), now);
                aggregatedPriceService.updateAggregatedPrice(dto);
            }

            LocalDateTime end = LocalDateTime.now();
            logger.info("[{} Scheduled update] End: {} | {}seconds", symbol, end, Duration.between(start, end).getSeconds());

        } catch (Exception e) {
            logger.info("[{} Scheduled update] Exception: no update is done", symbol, e);
        }
    }
}
