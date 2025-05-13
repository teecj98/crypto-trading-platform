package com.teecj.crypto_trading_platform.trade.utils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

public class PriceCalculator {

    private PriceCalculator() {
    }

    /**
     * A best bid is the 1 with the highest value
     * @param bids
     * @return best bid
     */
    public static BigDecimal findBestBid(BigDecimal... bids) {
        return Collections.max(Arrays.asList(bids));
    }

    /**
     * A best ask is the 1 with the lowest value
     * @param asks
     * @return best ask
     */
    public static BigDecimal findBestAsk(BigDecimal... asks) {
        return Collections.min(Arrays.asList(asks));
    }
}
