package com.teecj.crypto_trading_platform.trade.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teecj.crypto_trading_platform.auth.services.CurrentUserService;
import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.common.constant.Symbol;
import com.teecj.crypto_trading_platform.common.constant.TradeType;
import com.teecj.crypto_trading_platform.trade.entities.Wallet;
import com.teecj.crypto_trading_platform.trade.models.AggregatedPriceDTO;
import com.teecj.crypto_trading_platform.trade.models.TradeRequest;
import com.teecj.crypto_trading_platform.trade.repositories.TradeRepository;
import com.teecj.crypto_trading_platform.trade.repositories.WalletRepository;
import com.teecj.crypto_trading_platform.trade.services.AggregatedPriceService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.hasSize;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
class TradeControllerIT {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    AggregatedPriceService aggregatedPriceService;
    @Autowired
    CurrentUserService currentUserService;
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    TradeRepository tradeRepository;

    @Autowired
    ObjectMapper objectMapper;


    List<TradeRequest> requests = List.of(
            new TradeRequest(TradeType.BUY, Symbol.BTCUSDT, new BigDecimal("0.2")),
            new TradeRequest(TradeType.BUY, Symbol.ETHUSDT, new BigDecimal("0.3")),
            new TradeRequest(TradeType.SELL, Symbol.BTCUSDT, new BigDecimal("0.05")),
            new TradeRequest(TradeType.SELL, Symbol.ETHUSDT, new BigDecimal("0.15"))
    );

    @BeforeEach
    public void setup() {
        OffsetDateTime now = OffsetDateTime.now();
        AggregatedPriceDTO price1 = new AggregatedPriceDTO(Symbol.BTCUSDT, new BigDecimal("103250.69"), new BigDecimal("103250.00"), now);
        aggregatedPriceService.updateAggregatedPrice(price1);

        AggregatedPriceDTO price2 = new AggregatedPriceDTO(Symbol.ETHUSDT, new BigDecimal("2592.14"), new BigDecimal("2591.77"), now);
        aggregatedPriceService.updateAggregatedPrice(price2);
    }


    @Test
    public void viewHistory_WhenNoTransactionIsDone() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_TRADING_HISTORY + "?pageNumber=0&pageSize=10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    @DirtiesContext
    public void dataIntegrity_whenTradeConcurrentlyWithRaceCondition() throws Exception {

        TradeRequest request = requests.getFirst();

        // run 100 concurrent buy BTCUSDT requests
        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    mockMvc.perform(MockMvcRequestBuilders.post(Api.POST_TRADING)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON));
                } catch (Exception e) {
                    throw new RuntimeException(e); // ignore handling for test
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // get wallet balance
        Wallet btcWallet = walletRepository.findByUserIdAndCurrency(currentUserService.getCurrentUser().getId(), Currency.BTC.toString());

        // get trading count
        long transactionCounts = tradeRepository.count();
        BigDecimal actualTotalAmountBought = request.amount().multiply(BigDecimal.valueOf(transactionCounts));

        // assert data integrity, btc bought = btc wallet balance
        Assertions.assertTrue(actualTotalAmountBought.compareTo(btcWallet.getBalance()) == 0);

    }

    @Test
    @DirtiesContext
    public void viewHistoryWithCorrectPageSize_WhenHaveTransactions() throws Exception {

        for (TradeRequest request : requests) {
            mockMvc.perform(MockMvcRequestBuilders.post(Api.POST_TRADING)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        }

        int expectedPageSize = 4;
        var mock = mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_TRADING_HISTORY + "?pageNumber=0&pageSize=" + expectedPageSize))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(expectedPageSize)));

        for (int i = 0; i < requests.size(); i++) {
            TradeRequest request = requests.get(i);

            mock
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + i + "].tradeType").value(request.type().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + i + "].symbol").value(request.symbol().toString()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$[" + i + "].amount").value(request.amount().toString()));
        }

    }

    @Test
    public void errorHistoryView_WhenViewMoreThan20PerPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(Api.GET_TRADING_HISTORY + "?pageNumber=0&pageSize=21"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }


    @Test
    @DirtiesContext
    public void cannotSell_WhenTargetWalletNotEnoughBalance() throws Exception {
        TradeRequest buyRequest = new TradeRequest(TradeType.BUY, Symbol.ETHUSDT, new BigDecimal("0.3"));
        TradeRequest sellRequest = new TradeRequest(TradeType.SELL, Symbol.ETHUSDT, new BigDecimal("0.1"));

        // buy 0.3
        mockMvc.perform(MockMvcRequestBuilders.post(Api.POST_TRADING)
                        .content(objectMapper.writeValueAsString(buyRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        // sell 0.3 ok
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(MockMvcRequestBuilders.post(Api.POST_TRADING)
                            .content(objectMapper.writeValueAsString(sellRequest))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
        }

        // fail to sell
        mockMvc.perform(MockMvcRequestBuilders.post(Api.POST_TRADING)
                        .content(objectMapper.writeValueAsString(sellRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}