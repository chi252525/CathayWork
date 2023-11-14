package com.example.configuration;

import com.example.modules.HttpProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
@Data
@Component
@ConfigurationProperties(prefix = "coin-desk.api")
public class CoinDeskProperties implements HttpProperties {
    private String endpoint;
    private String proxyHost;
    private String proxyUrl;
    private Integer proxyPort;
    private String username;
    private String password;
    private int connectionTimeOut;


    public Proxy getProxy() {
        try {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.proxyHost, this.proxyPort));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getEndpoint() {
        return this.endpoint;
    }

    @Override
    public String getProxyUrl() {
        return this.proxyUrl;
    }

    @Override
    public Integer getProxyPort() {
        return this.proxyPort;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public int getConnectionTimeOut() {
        return this.connectionTimeOut;
    }
}
