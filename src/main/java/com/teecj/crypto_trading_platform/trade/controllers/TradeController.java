package com.teecj.crypto_trading_platform.trade.controllers;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.auth.services.CurrentUserService;
import com.teecj.crypto_trading_platform.trade.models.BuyTradeDTO;
import com.teecj.crypto_trading_platform.trade.models.SellTradeDTO;
import com.teecj.crypto_trading_platform.trade.models.TradeRequest;
import com.teecj.crypto_trading_platform.trade.models.TradingHistoryDataDTO;
import com.teecj.crypto_trading_platform.trade.services.BestBuySellTradingService;
import com.teecj.crypto_trading_platform.trade.services.TradeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.teecj.crypto_trading_platform.trade.controllers.Api.POST_TRADING;

@RestController
public class TradeController {

    private final static Logger logger = LoggerFactory.getLogger(TradeController.class);

    private final CurrentUserService currentUserService;
    private final TradeService tradeService;
    private final BestBuySellTradingService bestBuySellTradingService;

    @Autowired
    public TradeController(CurrentUserService currentUserService, TradeService tradeService, BestBuySellTradingService bestBuySellTradingService) {
        this.currentUserService = currentUserService;
        this.tradeService = tradeService;
        this.bestBuySellTradingService = bestBuySellTradingService;
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

    @PostMapping(POST_TRADING)
    public ResponseEntity<String> doTradeRequest(@Validated @RequestBody TradeRequest request) {
        User currentUser = currentUserService.getCurrentUser();
        try {
            switch (request.type()) {
                case BUY ->
                        bestBuySellTradingService.performBuyTrade(new BuyTradeDTO(request.symbol(), request.amount()), currentUser);
                case SELL ->
                        bestBuySellTradingService.performSellTrade(new SellTradeDTO(request.symbol(), request.amount()), currentUser);
            }

        } catch (Exception e) {
            logger.error("[do trade request] Failed | userId: {} | {} ", currentUser.getId(), request, e);
            return ResponseEntity.badRequest().build();
        }

        logger.info("[do trade request] Trade request completed | userId: {}", currentUser.getId());
        return ResponseEntity.noContent().build();

    }
}
