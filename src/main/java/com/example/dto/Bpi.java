package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class Bpi {
    private String code;
    private String symbol;
    private BigDecimal rate;
    private String description;
    @JsonProperty("rate_float")
    private BigDecimal rateFloat;
}
