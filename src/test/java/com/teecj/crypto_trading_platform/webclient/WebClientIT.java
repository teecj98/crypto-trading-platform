package com.teecj.crypto_trading_platform.webclient;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.webclient.impl.BinanceWebClient;
import com.teecj.crypto_trading_platform.webclient.impl.HuobiWebClient;
import com.teecj.crypto_trading_platform.webclient.models.AggregatedPriceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@SpringBootTest
@ActiveProfiles("dev")
public class WebClientIT {

    private static final Logger logger = LoggerFactory.getLogger(WebClientIT.class);

    private final BinanceWebClient binanceWebClient;
    private final HuobiWebClient huobiWebClient;

    @Autowired
    public WebClientIT(BinanceWebClient binanceWebClient, HuobiWebClient huobiWebClient) {
        this.binanceWebClient = binanceWebClient;
        this.huobiWebClient = huobiWebClient;
    }

    @Test
    public void testGetBinanceBTCUSDT() {
        AggregatedPriceResponse response = this.binanceWebClient.getAggregatedPrice(Symbol.BTCUSDT).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(Symbol.BTCUSDT, response.symbol());
        Assertions.assertTrue(response.bidPrice().compareTo(BigDecimal.ZERO)  > 0);
        Assertions.assertTrue(response.askPrice().compareTo(BigDecimal.ZERO)  > 0);

        logger.info(response::toString);
    }

    @Test
    public void testGetBinanceETHUSDT() {
        AggregatedPriceResponse response = this.binanceWebClient.getAggregatedPrice(Symbol.ETHUSDT).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(Symbol.ETHUSDT, response.symbol());
        Assertions.assertTrue(response.bidPrice().compareTo(BigDecimal.ZERO)  > 0);
        Assertions.assertTrue(response.askPrice().compareTo(BigDecimal.ZERO)  > 0);

        logger.info(response::toString);
    }

    @Test
    public void testGetHuobiBTCUSDT() {
        AggregatedPriceResponse response = this.huobiWebClient.getAggregatedPrice(Symbol.BTCUSDT).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(Symbol.BTCUSDT, response.symbol());
        Assertions.assertTrue(response.bidPrice().compareTo(BigDecimal.ZERO)  > 0);
        Assertions.assertTrue(response.askPrice().compareTo(BigDecimal.ZERO)  > 0);

        logger.info(response::toString);
    }

    @Test
    public void testGetHuobiETHUSDT() {
        AggregatedPriceResponse response = this.huobiWebClient.getAggregatedPrice(Symbol.ETHUSDT).block();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(Symbol.ETHUSDT, response.symbol());
        Assertions.assertTrue(response.bidPrice().compareTo(BigDecimal.ZERO)  > 0);
        Assertions.assertTrue(response.askPrice().compareTo(BigDecimal.ZERO)  > 0);
        logger.info(response::toString);
    }


}
