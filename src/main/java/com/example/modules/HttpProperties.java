package com.example.modules;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.util.HashMap;
import java.util.Map;

public interface HttpProperties {

    public String getEndpoint();

    public String getProxyUrl();

    public Integer getProxyPort();

    public String getUsername();

    public String getPassword();

    public int getConnectionTimeOut();

    public default HttpHost getProxyGid() {
        if (StringUtils.isBlank(getProxyUrl()))
            return null;
        int port = getProxyPort() == null ? 0 : getProxyPort();
        return new HttpHost(getProxyUrl(), port);
    }

    public default Map<String, String> getHeaderMap() {
        return new HashMap<>();
    }

    public default Boolean disableAutomaticRetries() {
        return false;
    }
}
