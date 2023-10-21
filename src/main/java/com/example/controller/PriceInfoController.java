package com.example.controller;

import com.example.dto.Bpi;
import com.example.dto.CurrencyInfo;
import com.example.dto.PriceInfo;
import com.example.service.CurrencyService;
import com.example.service.PriceInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/price")
public class PriceInfoController {
    @Autowired
    private PriceInfoService priceInfoService;

    //    呼叫 coindesk API，解析其下行內容與資料轉換，並實作新的 API。
    @GetMapping(value = "/transfer", produces = "application/json")
    public ResponseEntity getTransfer() {
        try {
            PriceInfo priceInfo = priceInfoService.getCoinDeskAPI();
            return ResponseEntity.ok(priceInfo);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //    呼叫 coindesk 的 API，並進行資料轉換，組成新 API。
    //    此新 API 提供：
    //    A. 更新時間（時間格式範例：1990/01/01 00:00:00）。
    //    B. 幣別相關資訊（幣別，幣別中文名稱，以及匯率）。
    @GetMapping(value = "/info", produces = "application/json")
    public ResponseEntity getPriceInfo() {
        try {
            PriceInfo priceInfo = priceInfoService.getCoinDeskAPI();
            String updateTime = priceInfo.getTime().getUpdated();
            //時間格式範例：1990/01/01 00:00:00
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, uuuu HH:mm:ss z", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(updateTime, formatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
            updateTime = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Taipei"))
                    .format(outputFormatter);
            Map<String, Bpi> bpiMap = priceInfo.getBpi();
            Bpi usdBpi = bpiMap.get("USD");
            String currencyCode = usdBpi.getCode();
            String currencyName = usdBpi.getDescription();
            BigDecimal price = usdBpi.getRate();
            return ResponseEntity.ok(new CurrencyInfo(updateTime, currencyCode, currencyName, price));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
