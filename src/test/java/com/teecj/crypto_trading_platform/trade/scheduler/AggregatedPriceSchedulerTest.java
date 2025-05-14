package com.teecj.crypto_trading_platform.trade.scheduler;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import com.teecj.crypto_trading_platform.trade.services.AggregatedPriceService;
import com.teecj.crypto_trading_platform.webclient.impl.BinanceWebClient;
import com.teecj.crypto_trading_platform.webclient.impl.HuobiWebClient;
import com.teecj.crypto_trading_platform.webclient.models.AggregatedPriceResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(classes = AggregatedPriceScheduler.class)
class AggregatedPriceSchedulerTest {

    private final AggregatedPriceScheduler scheduler;

    @MockitoBean
    BinanceWebClient binanceWebClient;
    @MockitoBean
    HuobiWebClient huobiWebClient;
    @MockitoBean
    AggregatedPriceService aggregatedPriceService;

    @Autowired
    public AggregatedPriceSchedulerTest(AggregatedPriceScheduler scheduler, BinanceWebClient binanceWebClient, HuobiWebClient huobiWebClient, AggregatedPriceService aggregatedPriceService) {
        this.scheduler = scheduler;
        this.binanceWebClient = binanceWebClient;
        this.huobiWebClient = huobiWebClient;
        this.aggregatedPriceService = aggregatedPriceService;
    }


    BigDecimal bestBidPrice = new BigDecimal("10001.999");
    BigDecimal bestAskPrice = new BigDecimal("10000.9");

    Consumer<AggregatedPriceDTO> getAssertFunc(AggregatedPriceDTO expected) {
        return actual -> {
            assertEquals(expected.symbol(), actual.symbol());
            assertEquals(expected.bid(), actual.bid());
            assertEquals(expected.ask(), actual.ask());
        };
    }

    @Test
    void compareSourcesAndUpdate_updateTheBestBTCUSDT() {
        Symbol symbol = Symbol.BTCUSDT;

        when(binanceWebClient.getAggregatedPrice(symbol))
                .thenReturn(Mono.just(new AggregatedPriceResponse(symbol,bestBidPrice, new BigDecimal("10000.999"))));
        when(huobiWebClient.getAggregatedPrice(symbol))
                .thenReturn(Mono.just(new AggregatedPriceResponse(symbol,new BigDecimal("10001.9"), bestAskPrice)));

        AggregatedPriceDTO expected = new AggregatedPriceDTO(symbol, bestBidPrice, bestAskPrice, OffsetDateTime.now());
        Consumer<AggregatedPriceDTO> assertFunc = getAssertFunc(expected);

        when(aggregatedPriceService.updateAggregatedPrice(assertArg(assertFunc))).thenReturn(true);
        scheduler.updateAggregatedPriceOfBTCUSDT();
        verify(aggregatedPriceService).updateAggregatedPrice(assertArg(assertFunc));
    }

    @Test
    void when1NoResponse_useAnotherAsTheBest() {
        Symbol symbol = Symbol.ETHUSDT;
        when(binanceWebClient.getAggregatedPrice(symbol)).thenReturn(Mono.just(new AggregatedPriceResponse(symbol,bestBidPrice, bestAskPrice)));
        // no response
        when(huobiWebClient.getAggregatedPrice(symbol)).thenReturn(Mono.empty());

        AggregatedPriceDTO expected = new AggregatedPriceDTO(symbol, bestBidPrice, bestAskPrice, OffsetDateTime.now());
        Consumer<AggregatedPriceDTO> assertFunc = getAssertFunc(expected);

        when(aggregatedPriceService.updateAggregatedPrice(assertArg(assertFunc))).thenReturn(true);
        scheduler.updateAggregatedPriceOfETHUSDT();
        verify(aggregatedPriceService).updateAggregatedPrice(assertArg(assertFunc));
    }

}