package com.teecj.crypto_trading_platform.trade.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureCache
@AutoConfigureWebMvc
@AutoConfigureMockMvc
class AggregatePriceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void getAggregatePrice() throws Exception {
        Thread.sleep(3000); // allow for aggregation price update
        Symbol symbol = Symbol.BTCUSDT;
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_SYMBOL_AGGREGATE_PRICE, Symbol.BTCUSDT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        AggregatedPriceDTO actual = objectMapper.readValue(response.getResponse().getContentAsString(), AggregatedPriceDTO.class);

        assertEquals(symbol, actual.symbol());
        assertTrue(actual.bid().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(actual.ask().compareTo(BigDecimal.ZERO) > 0);


    }

    @Test
    void getUnknownAggregatePrice() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_SYMBOL_AGGREGATE_PRICE, "UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}