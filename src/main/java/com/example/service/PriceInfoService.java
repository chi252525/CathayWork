package com.example.service;

import com.example.dto.Bpi;
import com.example.dto.PriceInfo;
import com.example.dto.Time;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PriceInfoService {

    public PriceInfo getCoinDeskAPI() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.coindesk.com/v1/bpi/currentprice.json");
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String json = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> data = mapper.readValue(json, Map.class);
            Map<String, Object> innerDataTime = (Map<String, Object>) data.get("time");
            String disclaimer = (String) data.get("disclaimer");
            String chartName = (String) data.get("chartName");
            Map<String, Object> bpi = (Map<String, Object>) data.get("bpi");
            return parseDataToDto(innerDataTime, disclaimer, chartName, bpi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private PriceInfo parseDataToDto(Map<String, Object> innerDataTime, String disclaimer, String chartName, Map<String, Object> bpi) {
        PriceInfo priceInfo = new PriceInfo();
        Time time = new Time();
        time.setUpdated(String.valueOf(innerDataTime.get("updated")));
        time.setUpdateduk(String.valueOf(innerDataTime.get("updateduk")));
        time.setUpdatedISO(String.valueOf(innerDataTime.get("updatedISO")));
        priceInfo.setTime(time);
        priceInfo.setDisclaimer(disclaimer);
        priceInfo.setChartName(chartName);
        Map<String, Bpi> bpiMap = new HashMap<>();
        for (String coinKey : bpi.keySet()) {
            Map<String, Object> bpiData = (Map<String, Object>) bpi.get(coinKey);
            Bpi bpiDto = new Bpi();
            bpiDto.setCode((String) bpiData.get("code"));
            bpiDto.setSymbol((String) bpiData.get("symbol"));
            bpiDto.setRate(new BigDecimal(String.valueOf(bpiData.get("rate")).replaceAll(",", "")));
            bpiDto.setDescription((String) bpiData.get("description"));
            bpiDto.setRateFloat(new BigDecimal(String.valueOf(bpiData.get("rate_float"))));
//            System.out.println(new Gson().toJson(bpiDto));
            bpiMap.put(coinKey, bpiDto);
        }
        priceInfo.setBpi(bpiMap);
//        System.out.println(new Gson().toJson(priceInfo));
        return priceInfo;
    }
}
