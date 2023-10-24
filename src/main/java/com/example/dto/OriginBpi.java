package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class OriginBpi {
    private String code;
    private String symbol;
    private String rate;
    private String description;
    @JsonProperty("rate_float")
    private String rateFloat;
}
