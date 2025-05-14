package com.teecj.crypto_trading_platform.trade.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.auth.services.CurrentUserService;
import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.common.constant.TradeType;
import com.teecj.crypto_trading_platform.trade.models.TradingDTO;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceDTO;
import com.teecj.crypto_trading_platform.trade.services.TradeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
class WalletControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TradeService tradeService;

    @Autowired
    CurrentUserService currentUserService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void expectUSDTWalletHave50000_whenNoTransactionIsDone() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_USER_WALLET_BALANCE, Currency.USDT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value(Currency.USDT.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(50000));
    }

    @Test
    @DirtiesContext
    void expectChangedWalletBalance_whenBuySellTransactionsDone() throws Exception {

        BigDecimal originalAmount = BigDecimal.valueOf(50000);

        User user = currentUserService.getCurrentUser();
        TradingDTO buy20ETH = new TradingDTO(Symbol.ETHUSDT, TradeType.BUY, Currency.USDT, new BigDecimal("5000"), Currency.ETH, new BigDecimal("20"));
        tradeService.performTrade(buy20ETH, user);

        TradingDTO buy60ETH = new TradingDTO(Symbol.ETHUSDT, TradeType.BUY, Currency.USDT, new BigDecimal("15000"), Currency.ETH, new BigDecimal("60"));
        tradeService.performTrade(buy60ETH, user);

        TradingDTO sell10ETH = new TradingDTO(Symbol.ETHUSDT, TradeType.BUY, Currency.ETH, new BigDecimal("10"), Currency.USDT, new BigDecimal("3000"));
        tradeService.performTrade(sell10ETH, user);

        TradingDTO buy20BTC = new TradingDTO(Symbol.BTCUSDT, TradeType.BUY, Currency.USDT, new BigDecimal("15000"), Currency.BTC, new BigDecimal("20"));
        tradeService.performTrade(buy20BTC, user);

        TradingDTO sell10BTC = new TradingDTO(Symbol.BTCUSDT, TradeType.BUY, Currency.BTC, new BigDecimal("10"), Currency.USDT, new BigDecimal("8000"));
        tradeService.performTrade(sell10BTC, user);



        BigDecimal expectedUSDTBalance = originalAmount.subtract(buy20ETH.deductAmount()).subtract(buy60ETH.deductAmount()).add(sell10ETH.depositAmount())
                .subtract(buy20BTC.deductAmount()).add(sell10BTC.depositAmount());

        BigDecimal expectedBTCBalance = BigDecimal.ZERO.add(buy20BTC.depositAmount()).subtract(sell10BTC.deductAmount());

        BigDecimal expectedETHBalance = BigDecimal.ZERO.add(buy20ETH.depositAmount()).add(buy60ETH.depositAmount()).subtract(sell10ETH.deductAmount());


        MvcResult r1 = mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_USER_WALLET_BALANCE, Currency.USDT))
                .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        WalletBalanceDTO actual1 = objectMapper.readValue(r1.getResponse().getContentAsString(), WalletBalanceDTO.class);
        Assertions.assertEquals(Currency.USDT, actual1.currency());
        Assertions.assertTrue(actual1.balance().compareTo(expectedUSDTBalance) == 0);

        MvcResult r2= mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_USER_WALLET_BALANCE, Currency.BTC))
                .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
        WalletBalanceDTO actual2 = objectMapper.readValue(r2.getResponse().getContentAsString(), WalletBalanceDTO.class);
        Assertions.assertEquals(Currency.BTC, actual2.currency());
        Assertions.assertTrue(actual2.balance().compareTo(expectedBTCBalance) == 0);

        MvcResult r3 = mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_USER_WALLET_BALANCE, Currency.ETH))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        WalletBalanceDTO actual3 = objectMapper.readValue(r3.getResponse().getContentAsString(), WalletBalanceDTO.class);
        Assertions.assertEquals(Currency.ETH, actual3.currency());
        Assertions.assertTrue(actual3.balance().compareTo(expectedETHBalance) == 0);
    }
}