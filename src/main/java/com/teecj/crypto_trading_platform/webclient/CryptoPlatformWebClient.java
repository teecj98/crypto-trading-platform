package com.teecj.crypto_trading_platform.webclient;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.webclient.models.AggregatedPriceResponse;
import reactor.core.publisher.Mono;

public interface CryptoPlatformWebClient {
    Mono<AggregatedPriceResponse> getAggregatedPrice(Symbol symbol);
}
