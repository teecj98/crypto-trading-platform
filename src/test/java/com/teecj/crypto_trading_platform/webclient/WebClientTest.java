package com.teecj.crypto_trading_platform.webclient;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.webclient.impl.BinanceWebClient;
import com.teecj.crypto_trading_platform.webclient.impl.HuobiWebClient;
import com.teecj.crypto_trading_platform.webclient.models.AggregatedPriceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("dev")
public class WebClientTest {

    private final BinanceWebClient binanceWebClient;
    private final HuobiWebClient huobiWebClient;


    @MockitoBean
    private WebClient webClientMock;


    @Autowired
    public WebClientTest(BinanceWebClient binanceWebClient, HuobiWebClient huobiWebClient) {
        this.binanceWebClient = binanceWebClient;
        this.huobiWebClient = huobiWebClient;
    }

    private WebClient.ResponseSpec mockResponseSpec() {

        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);

        WebClient.RequestHeadersSpec requestHeadersSpecMock = mock(WebClient.RequestHeadersSpec.class);
        // anyString as it is not the concern of the test
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestHeadersSpecMock);

        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        return responseSpecMock;
    }

    @Test
    public void testHuobi_CorruptedJSON() {
        WebClient.ResponseSpec responseSpec = mockResponseSpec();
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("""
                        "status":corrupted
                        """));
        Assertions.assertNull(this.huobiWebClient.getAggregatedPrice(Symbol.BTCUSDT).block());
    }

    @Test
    public void testHuobi_ResponseError() {
        WebClient.ResponseSpec responseSpec = mockResponseSpec();
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("""
                        "status":"error"
                        """));
        Assertions.assertNull(this.huobiWebClient.getAggregatedPrice(Symbol.ETHUSDT).block());
    }

    @Test
    public void testHuobi_Timeout() {
        long start = System.currentTimeMillis();
        WebClient.ResponseSpec responseSpec = mockResponseSpec();
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.delay(Duration.ofSeconds(6)).thenReturn(""));
        assertNull(this.huobiWebClient.getAggregatedPrice(Symbol.BTCUSDT).block());

        // assert will timeout without hanging
        assertTrue(System.currentTimeMillis() - start < 10000);
    }

    @Test
    public void testBinance_ResponseError() {
        WebClient.ResponseSpec responseSpec = mockResponseSpec();
        when(responseSpec.bodyToMono(AggregatedPriceResponse.class))
                .thenReturn(Mono.error(new WebClientException("error") {
                }));
        assertNull(this.binanceWebClient.getAggregatedPrice(Symbol.ETHUSDT).block());
    }


    @Test
    public void testBinance_Timeout() {
        long start = System.currentTimeMillis();
        WebClient.ResponseSpec responseSpec = mockResponseSpec();
        when(responseSpec.bodyToMono(AggregatedPriceResponse.class))
                .thenReturn(Mono.delay(Duration.ofSeconds(6)).thenReturn(new AggregatedPriceResponse(Symbol.BTCUSDT, BigDecimal.ZERO, BigDecimal.ZERO)));
        assertNull(this.binanceWebClient.getAggregatedPrice(Symbol.BTCUSDT).block());

        // assert will timeout without hanging
        assertTrue(System.currentTimeMillis() - start < 10000);
    }
}
