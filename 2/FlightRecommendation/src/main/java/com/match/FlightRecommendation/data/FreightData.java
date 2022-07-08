package com.match.FlightRecommendation.data;

import java.io.Serializable;

public class FreightData extends Base implements Serializable {

    private char cabin; // 舱位，1位

    private String amount;  // 票价，8位

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
