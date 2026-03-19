package com.aquariux.demo.controller;

import com.aquariux.demo.dto.request.TradeRequest;
import com.aquariux.demo.dto.response.TradeHistoryItemResponse;
import com.aquariux.demo.dto.response.TradeResponse;
import com.aquariux.demo.service.TradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/history")
    public Page<TradeHistoryItemResponse> history(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        return tradeService.findByUserIdOrderByExecutedAtDesc(page, size);
    }
}
