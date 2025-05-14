package com.teecj.crypto_trading_platform.trade.controllers;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.auth.services.CurrentUserService;
import com.teecj.crypto_trading_platform.common.constant.Currency;
import com.teecj.crypto_trading_platform.trade.models.WalletBalanceDTO;
import com.teecj.crypto_trading_platform.trade.services.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.teecj.crypto_trading_platform.trade.controllers.Api.GET_USER_WALLET_BALANCE;

@RestController
public class WalletController {

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
        WalletBalanceDTO dto = walletService.findBalanceByUserId(user.getId(), currency);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dto);
    }
}
