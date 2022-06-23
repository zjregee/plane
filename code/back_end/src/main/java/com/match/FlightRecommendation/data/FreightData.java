package com.match.FlightRecommendation.data;

import java.io.Serializable;

public class FreightData extends Base implements Serializable {
    // 舱位，1位
    private char cabin;
    // 票价，8位，不足加空格
    private String amount;

    public FreightData(char cabin, String amount) {
        this.cabin = cabin;
        this.amount = amount;
    }

    public FreightData() {

    }

    public char getCabin() {
        return cabin;
    }

    public void setCabin(char cabin) {
        this.cabin = cabin;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
