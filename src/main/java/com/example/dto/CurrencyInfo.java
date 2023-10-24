package com.example.dto;

import com.example.enumeration.Coin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyInfo {
    private String updateTime;
    private String currencyCode;
    private String currencyName;
    private BigDecimal price;

    public CurrencyInfo(String updateTime, String currencyCode, String currencyName, BigDecimal price) {
        this.updateTime = updateTime;
        this.currencyCode = currencyCode;
        this.currencyName = Coin.getNameChByCode(currencyCode);
        this.price = price;
    }


}
