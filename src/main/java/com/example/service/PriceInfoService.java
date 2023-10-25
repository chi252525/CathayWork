package com.example.service;

import com.example.dto.Bpi;
import com.example.dto.PriceInfo;
import com.example.dto.Time;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PriceInfoService {

    @Value("${coinDesk.api.url}")
    private String coinDeskUrl;

    public String getCoinDeskAPIRsp() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(coinDeskUrl);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public PriceInfo getCoinDeskAPI() {
        String json = getCoinDeskAPIRsp();
        ObjectMapper objectMapper = new ObjectMapper();
        PriceInfo priceInfo = null;
        try {
            priceInfo = objectMapper.readValue(json, PriceInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return priceInfo;
    }


    public PriceInfo getCoinDeskTransferAPI() {
        String json = getCoinDeskAPIRsp();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> data = null;
        try {
            data = mapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> innerDataTime = (Map<String, Object>) data.get("time");
        String disclaimer = (String) data.get("disclaimer");
        String chartName = (String) data.get("chartName");
        Map<String, Object> bpi = (Map<String, Object>) data.get("bpi");
        return parseDataToDto(innerDataTime, disclaimer, chartName, bpi);
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
            bpiDto.setRate((String) bpiData.get("rate"));
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
