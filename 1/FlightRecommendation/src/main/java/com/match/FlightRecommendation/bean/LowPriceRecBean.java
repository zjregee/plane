package com.match.FlightRecommendation.bean;

import java.util.List;

public class LowPriceRecBean implements Comparable<LowPriceRecBean>{
    private List<String> carrier;

    private String FlightNo;

    private List<Character> cabin;

    private String price;

    private List<String> agencies;

    private String sequenceNum;

    private String startTime;

    private String endTime;

    private String departure;

    private String arrival;

    public LowPriceRecBean(List<String> carrier, String flightNo, List<Character> cabin, String price, List<String> agencies, String sequenceNum, String startTime, String endTime, String departure, String arrival) {
        this.carrier = carrier;
        FlightNo = flightNo;
        this.cabin = cabin;
        this.price = price;
        this.agencies = agencies;
        this.sequenceNum = sequenceNum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.departure = departure;
        this.arrival = arrival;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }
}
