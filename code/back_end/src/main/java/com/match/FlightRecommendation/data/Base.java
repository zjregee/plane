package com.match.FlightRecommendation.data;

import java.io.Serializable;

public class Base implements Serializable {
    // 航班承运人，2位
    private String carrier;
    // 起飞城市，3位
    private String departure;
    // 落地城市，3位
    private String arrival;

    public Base(String carrier, String departure, String arrival) {
        this.carrier = carrier;
        this.departure = departure;
        this.arrival = arrival;
    }

    public Base() {

    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
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
