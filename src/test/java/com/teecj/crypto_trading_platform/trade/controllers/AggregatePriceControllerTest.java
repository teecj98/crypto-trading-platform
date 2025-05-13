package com.teecj.crypto_trading_platform.trade.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import com.teecj.crypto_trading_platform.trade.services.AggregatedPriceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;

@WebMvcTest(AggregatePriceController.class)
class AggregatePriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    AggregatedPriceService aggregatedPriceService;

    @Test
    void getAggregatePrice() throws Exception {
        Symbol symbol = Symbol.BTCUSDT;
        AggregatedPriceDTO expected = new AggregatedPriceDTO(symbol, new BigDecimal("1000.11"), new BigDecimal("999"), OffsetDateTime.now());

        when(aggregatedPriceService.getAggregatedPrice(symbol)).thenReturn(expected);
        var response = mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_SYMBOL_AGGREGATE_PRICE, Symbol.BTCUSDT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String expectedJson = objectMapper.writeValueAsString(expected);
        System.out.println(response.getResponse().getContentAsString());
        System.out.println(expectedJson);
        Assertions.assertEquals(response.getResponse().getContentAsString(), expectedJson);
    }

    @Test
    void getUnknownAggregatePrice() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_SYMBOL_AGGREGATE_PRICE, "UNKNOWN"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}