package com.aquariux.demo.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TradeRequest {

    @NotBlank
    private String pair;

    @NotBlank
    private String side;

    @NotNull
    @DecimalMin(value = "0.00000001")
    private BigDecimal quantity;
}
