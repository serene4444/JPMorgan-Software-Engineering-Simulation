package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balance")
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Balance getBalance(@RequestParam("userId") Long userId){
        logger.info("Balance request for userId: {}", userId);
        
        return userRepository.findById(userId)
            .map(user -> {
                logger.info("Balance for userId: {} is {}", userId, user.getBalance());
                return new Balance(user.getBalance());
            })
            .orElseGet(() -> {
                logger.error("User not found for userId: {}", userId);
                return new Balance(0.0f);
            });
    }
}