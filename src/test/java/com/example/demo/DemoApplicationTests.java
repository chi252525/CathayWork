package com.example.demo;

import com.example.controller.CurrencyController;
import com.example.controller.PriceInfoController;
import com.example.dto.Bpi;
import com.example.dto.PriceInfo;
import com.example.entity.Currency;
import com.example.enumeration.Coin;
import com.example.repository.CurrencyRepository;
import com.example.service.CurrencyService;
import com.example.service.PriceInfoService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan(basePackages = "com.example")
@AutoConfigureMockMvc
class DemoApplicationTests {
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private PriceInfoService priceInfoService;
    @Autowired
    CurrencyRepository repository;
    @Autowired
    private CurrencyController currencyController;
    @Autowired
    private PriceInfoController priceInfoController;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        //save CoinDeskAPI Data to H2 db
        PriceInfo priceInfo = priceInfoService.getCoinDeskTransferAPI();
        Map<String, Bpi> bpiMap = priceInfo.getBpi();
        for (String coinKey : bpiMap.keySet()) {
            Bpi bpi = bpiMap.get(coinKey);
            Currency currency = new Currency(coinKey, Coin.valueOf(coinKey).getNameCh(), bpi.getRateFloat());
            repository.save(currency);
        }
        //測試資料是否都有存到資料庫
        List<Currency> savedCurrencies = repository.findAll();
        Assert.assertFalse(savedCurrencies.isEmpty());
        Assert.assertTrue(savedCurrencies.stream().anyMatch(currency -> "USD".equals(currency.getCode())));
        Assert.assertTrue(savedCurrencies.stream().anyMatch(currency -> "GBP".equals(currency.getCode())));
        Assert.assertTrue(savedCurrencies.stream().anyMatch(currency -> "EUR".equals(currency.getCode())));
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    //  1.  測試呼叫查詢幣別對應表資料 API，並顯示其內容
    @Test
    public void testGetCurrencyList() {
        List<Currency> resultList = currencyController.getAllCurrencies();
        List<Currency> currencyList = getCoinDeskAPI();
        assertThat(resultList).containsExactlyInAnyOrderElementsOf(currencyList);
    }

    //   2. 測試呼叫新增幣別對應表資料API。
    @Test
    public void testAddCurrency() {
        Currency newCurrency = new Currency("JPY", "日圓", new BigDecimal(0.22));
        currencyController.addCurrency(newCurrency);
        List<Currency> currencyListFromApi = getCoinDeskAPI();
        List<Currency> resultList = currencyController.getAllCurrencies();
        assertThat(resultList.size()).isNotEqualTo(currencyListFromApi.size());
        Currency savedCurrency = repository.findByCode("JPY");
        assertThat(savedCurrency).isNotNull();
        assertThat(savedCurrency.getCode()).isEqualTo("JPY");
        assertThat(savedCurrency.getName()).isEqualTo("日圓");
        assertThat(savedCurrency.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(0.22));
    }

    //3. 測試呼叫更新幣別對應表資料API，並顯示其內容。
    @Test
    public void testUpdateCurrency() {
        Currency currency = repository.findByCode("USD");
        currency.setPrice(new BigDecimal(0.38));
        currencyController.updateCurrency(currency);
        List<Currency> currencyListFromApi = getCoinDeskAPI();
        Currency usdApiCurrency = currencyListFromApi.stream().filter(e -> "USD".equals(e.getCode())).findFirst().get();
        Currency updatedCurrency = repository.findByCode("USD");
        assertThat(updatedCurrency.getCode()).isEqualTo("USD");
        assertThat(updatedCurrency.getName()).isEqualTo("美元");
        assertThat(updatedCurrency.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(0.38));
        assertThat(updatedCurrency.getPrice()).isNotEqualByComparingTo(usdApiCurrency.getPrice());
    }

    //    4. 測試呼叫刪除幣別對應表資料API。
    @Test
    public void testDeleteCurrency() {
        currencyController.deleteCurrency("USD");
        List<Currency> currencyListFromApi = getCoinDeskAPI();
        Currency usdApiCurrency = currencyListFromApi.stream().filter(e -> "USD".equals(e.getCode())).findFirst().orElse(null);
        Currency currency = repository.findByCode("USD");
        assertNull(currency);
        assertNotNull(usdApiCurrency);
    }

    //5. 測試呼叫 coindesk API，並顯示其內容。
    @Test
    public void testInfoAPIOK() throws Exception {
        String json = priceInfoService.getCoinDeskAPIRsp();
        mockMvc.perform(get("/price/info")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().json(json));
    }

    // 6.   測試呼叫資料轉換的API，並顯示其內容。
    @Test
    public void testTransferAPIOK() throws Exception {
        List<Currency> currencies = getCoinDeskAPI();
        for (Coin coin : Coin.values()) {
            Currency currency = currencies.stream().filter(e -> coin.name().equals(e.getCode())).findFirst().orElse(null);
            mockMvc.perform(get("/price/transfer/" + coin.name())
                            .contentType("application/json"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("currencyCode").value(currency.getCode()))
                    .andExpect(jsonPath("currencyName").value(currency.getName()))
                    .andExpect(jsonPath("price").value(currency.getPrice()));
        }
    }

    private List<Currency> getCoinDeskAPI() {
        List<Currency> currencyList = new ArrayList<>();
        PriceInfo priceInfo = priceInfoService.getCoinDeskTransferAPI();
        Map<String, Bpi> bpiMap = priceInfo.getBpi();
        long index = 1;
        for (String coinKey : bpiMap.keySet()) {
            Bpi bpi = bpiMap.get(coinKey);
            Currency currency = new Currency(coinKey, Coin.valueOf(coinKey).getNameCh(), bpi.getRateFloat());
            currency.setId(index);
            index++;
            currencyList.add(currency);
        }
        return currencyList;
    }


}
