package com.aquariux.demo.external.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class HuobiTickerResponseDto {

    private List<HuobiBookTickerDto> data;

}
