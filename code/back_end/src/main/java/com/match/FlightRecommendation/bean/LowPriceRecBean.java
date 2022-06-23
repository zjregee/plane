package com.match.FlightRecommendation.bean;

import java.util.List;

public class LowPriceRecBean implements Comparable<LowPriceRecBean>{
    private List<String> carrier;

    private String FlightNo;

    private List<Character> cabin;

    private String price;

    private List<String> agencies;

    private String sequenceNum;

    public LowPriceRecBean(List<String> carrier, String flightNo, List<Character> cabin, String price, List<String> agencies, String sequenceNum) {
        this.carrier = carrier;
        FlightNo = flightNo;
        this.cabin = cabin;
        this.price = price;
        this.agencies = agencies;
        this.sequenceNum = sequenceNum;
    }

    public List<String> getCarrier() {
        return carrier;
    }

    public void setCarrier(List<String> carrier) {
        this.carrier = carrier;
    }

    public String getFlightNo() {
        return FlightNo;
    }

    public void setFlightNo(String flightNo) {
        FlightNo = flightNo;
    }

    public List<Character> getCabin() {
        return cabin;
    }

    public void setCabin(List<Character> cabin) {
        this.cabin = cabin;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int compareTo(LowPriceRecBean o) {
        return Integer.compare(Integer.parseInt(getPrice()), Integer.parseInt(o.getPrice()));
    }

    public List<String> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<String> agencies) {
        this.agencies = agencies;
    }

    public String getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(String sequenceNum) {
        this.sequenceNum = sequenceNum;
    }
}
