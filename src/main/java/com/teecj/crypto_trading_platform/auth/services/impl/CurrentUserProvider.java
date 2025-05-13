package com.teecj.crypto_trading_platform.auth.services.impl;

import com.teecj.crypto_trading_platform.auth.entities.User;
import com.teecj.crypto_trading_platform.auth.services.CurrentUserService;
import org.springframework.stereotype.Service;

import java.util.UUID;


/**
 * Assumption:
 * - user has always been authenticated & authorized, return a default user (assume has authorized role) for apis
 * - to replace with SecurityContextCurrentUserProvider when integrated with security
 */
@Service
public class CurrentUserProvider implements CurrentUserService {
    private User DEFAULT_USER = new User(1001L, UUID.fromString("0a539e34-e77a-4eb2-8a1f-8480fbaec61b"),"user001");

    @Override
    public User getCurrentUser() {
        return DEFAULT_USER;
    }
}
