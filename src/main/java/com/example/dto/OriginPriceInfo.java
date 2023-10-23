package com.example.dto;

import java.util.Map;

public class OriginPriceInfo {
    private Time time;
    private String disclaimer;
    private String chartName;
    private Map<String, OriginBpi> bpi;

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public Map<String, OriginBpi> getBpi() {
        return bpi;
    }

    public void setBpi(Map<String, OriginBpi> bpi) {
        this.bpi = bpi;
    }
}
