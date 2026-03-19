package com.aquariux.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletBalanceItemResponse {
    private String asset;
    private String balance;
}
