package com.example.client;

import com.example.configuration.CoinDeskProperties;
import com.example.dto.PriceInfo;
import com.example.exception.ExternalServiceException;
import com.example.modules.CommonHttpClient;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CoinDeskApiClient {
    public static final String PATH_GET = "/v1/bpi/currentprice.json";
    private CommonHttpClient commonHttpClient;
    @Autowired
    private CoinDeskProperties properties;

    @PostConstruct
    private void init() {
        this.commonHttpClient = new CommonHttpClient(this.properties);
    }

    public PriceInfo getCoinDeskAPI() {
        String jsonString = commonHttpClient.executeGetRequest(PATH_GET, null);
        PriceInfo response = new Gson().fromJson(jsonString, PriceInfo.class);
        return response;
    }

    void setProperties(CoinDeskProperties properties) {
        this.properties = properties;
        this.init();
    }

    public static void main(String[] args) throws ExternalServiceException {
        CoinDeskProperties properties1 = new CoinDeskProperties();
        //set...
        CoinDeskApiClient client = new CoinDeskApiClient();
        client.setProperties(properties1);
    }
}
