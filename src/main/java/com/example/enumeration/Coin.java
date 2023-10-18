package com.example.enumeration;

public enum Coin {

    USD("United States Dollar", "美元"),
    GBP("British Pound Sterling", "英鎊"),
    EUR("Euro", "歐元");

    public String getDescription() {
        return description;
    }

    public String getNameCh() {
        return nameCh;
    }

    private String description;
    private String nameCh;

    Coin(String description, String nameCh) {
        this.description = description;
        this.nameCh = nameCh;
    }

    public static String getNameChByCode(String code) {
        Coin coin = Coin.valueOf(code);
        if (coin != null) {
            return coin.getNameCh();
        }
        return null;
    }
}
