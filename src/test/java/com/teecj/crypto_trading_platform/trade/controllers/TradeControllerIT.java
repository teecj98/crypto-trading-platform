package com.teecj.crypto_trading_platform.trade.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureCache
@AutoConfigureWebMvc
@AutoConfigureMockMvc
class TradeControllerIT {


    @Autowired
    MockMvc mockMvc;


    @Test
    public void viewHistory_WhenNoTransactionIsDone() throws Exception {
         mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_TRADING_HISTORY + "?pageNumber=0&pageSize=10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

}