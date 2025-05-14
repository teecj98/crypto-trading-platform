package com.teecj.crypto_trading_platform.trade.controllers;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.auth.services.CurrentUserService;
import com.teecj.crypto_trading_platform.trade.models.TradingHistoryDataDTO;
import com.teecj.crypto_trading_platform.trade.services.TradeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TradeController {

    private final static Logger logger = LoggerFactory.getLogger(TradeController.class);

    private final CurrentUserService currentUserService;
    private final TradeService tradeService;

    @Autowired
    public TradeController(CurrentUserService currentUserService, TradeService tradeService) {
        this.currentUserService = currentUserService;
        this.tradeService = tradeService;
    }

    /**
     * Add pageable to avoid fetching all
     *
     * @param pageNumber
     * @param pageSize
     * @return history data
     */
    @GetMapping(Api.GET_TRADING_HISTORY)
    public ResponseEntity<List<TradingHistoryDataDTO>> getPagedTradingHistory(@Min(0) @RequestParam int pageNumber, @Max(20) @RequestParam int pageSize) {
        User currentUser = currentUserService.getCurrentUser();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return ResponseEntity.ok(tradeService.findTradeTransactions(currentUser.getId(), pageable));
    }


}
