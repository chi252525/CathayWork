package com.example.demo;

import com.example.controller.CurrencyController;
import com.example.controller.PriceInfoController;
import com.example.dto.CurrencyInfo;
import com.example.entity.Currency;
import com.example.service.CurrencyService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class DemoApplicationTests {
    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyController currencyController;
    @InjectMocks
    private PriceInfoController priceInfoController;
    @Mock
    private CloseableHttpClient httpClient;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //    測試呼叫查詢幣別對應表資料 API，並顯示其內容
    @Test
    public void testGetCurrencyByCode() {
        Currency currency = new Currency("USD", "US Dollar");
        when(currencyService.findByCode("USD")).thenReturn(currency);
        Currency result = currencyController.getCurrencyByCode("USD");
        assertEquals(currency, result);
        verify(currencyService, times(1)).findByCode("USD");
    }

    //    測試呼叫新增幣別對應表資料 API。
    @Test
    public void testAddCurrency() {
        Currency currency = new Currency("USD", "US Dollar");
        when(currencyService.save(currency)).thenReturn(currency);
        Currency result = currencyController.addCurrency(currency);
        assertEquals(currency, result);
    }

    //    測試呼叫更新幣別對應表資料 API，並顯示其內容。
    @Test
    public void testUpdateCurrency() {
        Currency existingCurrency = new Currency("USD", "US Dollar");
        existingCurrency.setId(1L);
        when(currencyService.findById(1L)).thenReturn(Optional.of(existingCurrency));
        Currency updatedCurrency = new Currency("EUR", "Euro");
        updatedCurrency.setId(1L);
        when(currencyService.save(existingCurrency)).thenReturn(updatedCurrency);
        Currency result = currencyController.updateCurrency(1L, updatedCurrency);
        assertEquals(updatedCurrency, result);
        verify(currencyService, times(1)).findById(1L);
        verify(currencyService, times(1)).save(existingCurrency);
    }

    //    測試呼叫刪除幣別對應表資料 API。
    @Test
    public void testDeleteCurrency() {
        doNothing().when(currencyService).deleteById(1L);
        currencyController.deleteCurrency(1L);
        verify(currencyService, times(1)).deleteById(1L);
    }

    @Test
    public void testGetPriceInfo() throws IOException {
        CloseableHttpResponse httpResponse = Mockito.mock(CloseableHttpResponse.class);

        when(httpClient.execute(Mockito.any())).thenReturn(httpResponse);
        ResponseEntity responseEntity = priceInfoController.getPriceInfo();


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());


        CurrencyInfo currencyInfo = (CurrencyInfo) responseEntity.getBody();
        assertEquals("USD", currencyInfo.getCurrencyCode());
        assertEquals("美元", currencyInfo.getCurrencyName());
        assertEquals(new BigDecimal("28307.8651"), currencyInfo.getPrice());
    }
}
