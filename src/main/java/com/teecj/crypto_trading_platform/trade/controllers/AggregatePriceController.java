package com.teecj.crypto_trading_platform.trade.controllers;

import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import com.teecj.crypto_trading_platform.trade.services.AggregatedPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

import static com.teecj.crypto_trading_platform.trade.controllers.Api.GET_SYMBOL_AGGREGATE_PRICE;

@RestController
public class AggregatePriceController {

    private static final Logger logger = LoggerFactory.getLogger(AggregatePriceController.class);

    @Value("${api.cache.best.aggregated.price.max-age.ms}")
    int cacheMaxAge;

    private final AggregatedPriceService aggregatedPriceService;

    @Autowired
    public AggregatePriceController(final AggregatedPriceService aggregatedPriceService) {
        this.aggregatedPriceService = aggregatedPriceService;
    }

    @GetMapping(GET_SYMBOL_AGGREGATE_PRICE)
    public ResponseEntity<AggregatedPriceDTO> getBestAggregatedPrice(@PathVariable("symbol") Symbol symbol) {
        AggregatedPriceDTO dto = aggregatedPriceService.getAggregatedPrice(symbol);
        if (dto == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .cacheControl(CacheControl.maxAge(cacheMaxAge, TimeUnit.MILLISECONDS).mustRevalidate())
                .body(dto);
    }
}
