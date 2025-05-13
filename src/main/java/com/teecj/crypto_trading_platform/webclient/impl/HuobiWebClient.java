package com.teecj.crypto_trading_platform.webclient.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.math.BigDecimal;
import java.time.Duration;

@Component
public class HuobiWebClient implements CryptoPlatformWebClient {

    private final Logger logger = LoggerFactory.getLogger(HuobiWebClient.class);

    @Value("${huobi.api.endpoint}")
    String huobiApiEndpoint;

    @Value("${webclient.http.timeout.ms}")
    private int timeoutInMs;

    private final WebClient webClient;

    @Autowired
    public HuobiWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * <a href="https://huobiapi.github.io/docs/spot/v1/en/#get-latest-aggregated-ticker">Reference</a>
     *
     */
    public  Mono<AggregatedPriceResponse> getAggregatedPrice(Symbol symbol) {
        String uri = huobiApiEndpoint + "market/detail/merged?symbol=" + symbol.toString().toLowerCase();
        logger.info("[Huobi getAggregatedPrice] start: {} ", uri);
        return this.webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .mapNotNull(res -> this.responseCustomizer(res, symbol, uri))
                .timeout(Duration.ofMillis(timeoutInMs))
                .onErrorResume(throwable -> {
                    logger.error("[Huobi getAggregatedPrice] Error: {}", uri, throwable);
                    return Mono.empty();
                });
    }

    private AggregatedPriceResponse responseCustomizer(String res, Symbol symbol, String uri) {
        JsonNode jsonNode;
        try {
            jsonNode = new ObjectMapper().readTree(res);

            String status = jsonNode.path("status").asText("error");
            if (!status.equals("ok")) {
                return null;
            }
            JsonNode tickNode = jsonNode.path("tick");

            String bidPrice = tickNode.path("bid").get(0).asText();
            String askPrice = tickNode.path("ask").get(0).asText();

            return new AggregatedPriceResponse(symbol, new BigDecimal(bidPrice), new BigDecimal(askPrice));
        } catch (Exception e) {
            logger.error("[Huobi getAggregatedPrice] Error: {}", uri, e);
            return null;
        }
    }


}
