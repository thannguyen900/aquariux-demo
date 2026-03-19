package com.aquariux.demo.dto.response;

import com.aquariux.demo.entity.TradeEntity;
import com.aquariux.demo.entity.TradeOrderEntity;
import com.aquariux.demo.entity.WalletEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TradeExecutionResult {
    private TradeOrderEntity order;
    private TradeEntity trade;
    private WalletEntity quoteWallet;
    private WalletEntity baseWallet;
}
