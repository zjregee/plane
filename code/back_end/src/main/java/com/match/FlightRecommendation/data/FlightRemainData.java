package com.match.FlightRecommendation.data;

import java.io.Serializable;

public class FlightRemainData extends Base implements Serializable {
    // 航班号，4位
    private String flightNo;

    // 起飞日期，12位
    private String departureDatetime;

    // 对应舱号余座数据，1位,0-9, A
    private char seatF;
    private char seatC;
    private char seatY;

    public FlightRemainData(char seatF, char seatC, char seatY) {
        this.seatF = seatF;
        this.seatC = seatC;
        this.seatY = seatY;
    }

    public FlightRemainData() {
        
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

    public char getSeatF() {
        return seatF;
    }

    public void setSeatF(char seatF) {
        this.seatF = seatF;
    }

    public char getSeatC() {
        return seatC;
    }

    public void setSeatC(char seatC) {
        this.seatC = seatC;
    }

    public char getSeatY() {
        return seatY;
    }

    public void setSeatY(char seatY) {
        this.seatY = seatY;
    }
}
