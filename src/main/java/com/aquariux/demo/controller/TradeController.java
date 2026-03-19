package com.aquariux.demo.controller;

import com.aquariux.demo.dto.request.TradeRequest;
import com.aquariux.demo.dto.response.TradeResponse;
import com.aquariux.demo.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping
    public TradeResponse trade(@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                               @Valid @RequestBody TradeRequest request) {
        return tradeService.executeTrade(request, idempotencyKey);
    }
}
