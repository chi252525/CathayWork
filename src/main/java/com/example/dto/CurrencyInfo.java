package com.example.dto;

import com.example.enumeration.Coin;

import java.math.BigDecimal;

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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


}
