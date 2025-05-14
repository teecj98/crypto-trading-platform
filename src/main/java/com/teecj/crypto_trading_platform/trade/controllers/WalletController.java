package com.teecj.crypto_trading_platform.trade.controllers;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.auth.services.CurrentUserService;
import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceDTO;
import com.teecj.crypto_trading_platform.trade.services.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.teecj.crypto_trading_platform.trade.controllers.Api.GET_USER_WALLET_BALANCE;
import static com.teecj.crypto_trading_platform.trade.controllers.Api.POST_CREATE_WALLET;

@RestController
public class WalletController {
    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private final CurrentUserService currentUserService;
    private final WalletService walletService;

    @Autowired
    public WalletController(CurrentUserService currentUserService, WalletService walletService) {
        this.currentUserService = currentUserService;
        this.walletService = walletService;
    }

    @GetMapping(GET_USER_WALLET_BALANCE)
    public ResponseEntity<WalletBalanceDTO> getBalance(@PathVariable Currency currency) {
        User user = currentUserService.getCurrentUser();
        WalletBalanceDTO dto = walletService.findBalanceByUserIdAndCurrency(user.getId(), currency);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dto);
    }

    /**
     * outside of scope, For new user, will need to create a wallet before to use it
     * @param currency
     * @return
     */
    @PostMapping(POST_CREATE_WALLET)
    public ResponseEntity<Void> createWallet(@PathVariable("currency") Currency currency) {
        User user = currentUserService.getCurrentUser();
        walletService.createNewWallet(user.getId(), currency);
        logger.info("[Wallet] Wallet created | userId: {}", user.getId());
        return ResponseEntity.noContent().build();
    }
}
