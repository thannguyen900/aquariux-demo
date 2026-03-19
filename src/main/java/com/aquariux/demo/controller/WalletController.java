package com.aquariux.demo.controller;

import com.aquariux.demo.dto.response.WalletBalanceResponse;
import com.aquariux.demo.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public WalletBalanceResponse getBalances() {
        return walletService.getWalletBalances();
    }
}
