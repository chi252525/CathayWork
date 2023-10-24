package com.example.dto;

import lombok.Data;

import java.util.Map;
@Data
public class PriceInfo {
    private Time time;
    private String disclaimer;
    private String chartName;
    private Map<String, Bpi> bpi;
}
