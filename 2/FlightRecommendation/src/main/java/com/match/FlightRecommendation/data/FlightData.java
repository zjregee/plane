package com.match.FlightRecommendation.data;

import java.io.Serializable;

public class FlightData extends Base implements Serializable {

    private String flightNo;    // 航班号，4位，补零

    private String departureDatetime;   // 起飞时间，12位

    private String arrivalDatetime; // 到达时间，12位

    public FlightData(String flightNo, String departureDatetime, String arrivalDatetime) {
        this.flightNo = flightNo;
        this.departureDatetime = departureDatetime;
        this.arrivalDatetime = arrivalDatetime;
    }

    public FlightData() {

    }

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public String getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(String departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public String getArrivalDatetime() {
        return arrivalDatetime;
    }

    public void setArrivalDatetime(String arrivalDatetime) {
        this.arrivalDatetime = arrivalDatetime;
    }
}
