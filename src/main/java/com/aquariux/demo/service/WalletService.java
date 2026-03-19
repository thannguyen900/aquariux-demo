package com.aquariux.demo.service;

import com.aquariux.demo.config.AppProperties;
import com.aquariux.demo.dto.response.WalletBalanceResponse;
import com.aquariux.demo.entity.WalletEntity;
import com.aquariux.demo.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AppProperties appProperties;

    public WalletBalanceResponse getWalletBalances() {
        Long userId = appProperties.getDemoUserId();
        List<WalletEntity> wallets = walletRepository.findByUserIdOrderByAssetAsc(userId);
        return WalletBalanceResponse.from(userId, wallets);
    }
}
