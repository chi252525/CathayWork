package com.example.demo;

import com.example.controller.CurrencyController;
import com.example.dto.Bpi;
import com.example.dto.PriceInfo;
import com.example.entity.Currency;
import com.example.enumeration.Coin;
import com.example.repository.CurrencyRepository;
import com.example.service.CurrencyService;
import com.example.service.PriceInfoService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = "com.example")
class DemoApplicationTests {
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private PriceInfoService priceInfoService;
    @Autowired
    CurrencyRepository repository;
    @Autowired
    private CurrencyController currencyController;

    @BeforeEach
    public void setUp() {
        //save CoinDeskAPI Data to H2 db
        PriceInfo priceInfo = priceInfoService.getCoinDeskAPI();
        Map<String, Bpi> bpiMap = priceInfo.getBpi();
        for (String coinKey : bpiMap.keySet()) {
            Bpi bpi = bpiMap.get(coinKey);
            Currency currency = new Currency(coinKey, Coin.valueOf(coinKey).getNameCh(), bpi.getRate());
            repository.save(currency);
        }
        //測試資料是否都有存到資料庫
        List<Currency> savedCurrencies = repository.findAll();
        Assert.assertFalse(savedCurrencies.isEmpty());
        Assert.assertTrue(savedCurrencies.stream().anyMatch(currency -> "USD".equals(currency.getCode())));
        Assert.assertTrue(savedCurrencies.stream().anyMatch(currency -> "GBP".equals(currency.getCode())));
        Assert.assertTrue(savedCurrencies.stream().anyMatch(currency -> "EUR".equals(currency.getCode())));
    }

    //    測試呼叫查詢幣別對應表資料 API，並顯示其內容
    @Test
    public void testGetCurrencyList() {
//        測試呼叫查詢幣別/list API, 資料是不是等於 coindesk API
        List<Currency> resultList = currencyController.getAllCurrencies();
        //呼叫coindesk API
        List<Currency> currencyList = getCoinDeskAPI();
//        比對其資料是否一致
        assertThat(resultList).containsExactlyInAnyOrderElementsOf(currencyList);
    }

    private List<Currency> getCoinDeskAPI() {
        List<Currency> currencyList = new ArrayList<>();
        PriceInfo priceInfo = priceInfoService.getCoinDeskAPI();
        Map<String, Bpi> bpiMap = priceInfo.getBpi();
        for (String coinKey : bpiMap.keySet()) {
            Bpi bpi = bpiMap.get(coinKey);
            Currency currency = new Currency(coinKey, Coin.valueOf(coinKey).getNameCh(), bpi.getRate());
            currencyList.add(currency);
        }
        return currencyList;
    }


}
