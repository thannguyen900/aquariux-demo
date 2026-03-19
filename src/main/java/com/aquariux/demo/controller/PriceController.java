package com.aquariux.demo.controller;

import com.aquariux.demo.dto.response.LatestPriceResponse;
import com.aquariux.demo.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final MarketPriceService marketPriceService;

    @GetMapping("/latest")
    public LatestPriceResponse getLatestPrice(@RequestParam String pair) {
        return marketPriceService.getLatestPrice(pair);
    }
}
