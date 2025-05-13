package com.teecj.crypto_trading_platform.trade.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;


class PriceCalculatorTest {

    @Test
    void findBestBid_highest() {
        BigDecimal expectedHighest = new BigDecimal("11212.999");
        BigDecimal actual = PriceCalculator.findBestBid(
                expectedHighest,
                new BigDecimal(1123),
                new BigDecimal("11212.998"),
                new BigDecimal(0),
                new BigDecimal("1.1"));

        Assertions.assertEquals(expectedHighest, actual);
    }

    @Test
    void findBestAsk_lowest() {
        BigDecimal expectedLowest = new BigDecimal(0);
        BigDecimal actual = PriceCalculator.findBestAsk(
                expectedLowest,
                new BigDecimal(1123),
                new BigDecimal("11212.998"),
                new BigDecimal("11212.999"),
                new BigDecimal("1.1"));

        Assertions.assertEquals(expectedLowest, actual);
    }
}