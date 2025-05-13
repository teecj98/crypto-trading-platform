package com.teecj.crypto_trading_platform.webclient.impl;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.webclient.CryptoPlatformWebClient;
import com.teecj.crypto_trading_platform.webclient.models.AggregatedPriceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class BinanceWebClient implements CryptoPlatformWebClient {
    private final Logger logger = LoggerFactory.getLogger(BinanceWebClient.class);

    @Value("${binance.api.endpoint}")
    String binanceApiEndpoint;

    @Value("${webclient.http.timeout.ms}")
    private int timeoutInMs;

    private final WebClient webClient;

    @Autowired
    public BinanceWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * <a href="https://developers.binance.com/docs/binance-spot-api-docs/testnet/rest-api/market-data-endpoints#symbol-order-book-ticker">Reference</a>
     */
    public Mono<AggregatedPriceResponse> getAggregatedPrice(Symbol symbol) {
        String uri = binanceApiEndpoint + "api/v3/ticker/bookTicker?symbol=" + symbol.toString().toUpperCase();
        logger.info("[Binance getAggregatedPrice] start: {} ", uri);
        return this.webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(AggregatedPriceResponse.class)
                .timeout(Duration.ofMillis(timeoutInMs))
                .onErrorResume(throwable -> {
                    logger.error("[Binance getAggregatedPrice] Error", throwable);
                    return Mono.empty();
                });
    }


}
