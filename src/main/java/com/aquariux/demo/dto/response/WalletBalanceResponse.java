package com.aquariux.demo.dto.response;

import com.aquariux.demo.entity.WalletEntity;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Getter
@Builder
public class WalletBalanceResponse {
    private static final String DEFAULT_BALANCE = "0.00000000";
    private static final int BALANCE_SCALE = 8;
    private Long userId;
    private List<WalletBalanceItemResponse> balances;

    public static WalletBalanceResponse from(Long userId, List<WalletEntity> wallets) {
        return WalletBalanceResponse.builder()
                .userId(userId)
                .balances(wallets.stream()
                        .map(wallet -> WalletBalanceItemResponse.builder()
                                .asset(wallet.getAsset())
                                .balance(wallet.getBalance().compareTo(BigDecimal.ZERO) <= 0 ?
                                        DEFAULT_BALANCE : String.valueOf(wallet.getBalance().setScale(BALANCE_SCALE, RoundingMode.HALF_UP)))
                                .build())
                        .toList())
                .build();
    }
}
