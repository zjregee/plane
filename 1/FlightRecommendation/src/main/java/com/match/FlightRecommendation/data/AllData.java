package com.match.FlightRecommendation.data;

import java.io.Serializable;
import java.util.List;

public class AllData extends Base implements Serializable {
    private FlightData flightData;

    private FlightRemainData flightRemainData;

    private List<FreightData> freightData;

    private FreightRuleData freightRuleData;

    public AllData(String carrier, String departure, String arrival) {
        super(carrier, departure, arrival);
    }

    public FlightData getFlightData() {
        return flightData;
    }

    public void setFlightData(FlightData flightData) {
        this.flightData = flightData;
    }

    public FlightRemainData getFlightRemainData() {
        return flightRemainData;
    }

    public void setFlightRemainData(FlightRemainData flightRemainData) {
        this.flightRemainData = flightRemainData;
    }

    public List<FreightData> getFreightData() {
        return freightData;
    }

    public void setFreightData(List<FreightData> freightData) {
        this.freightData = freightData;
    }

    public FreightRuleData getFreightRuleData() {
        return freightRuleData;
    }

    public void setFreightRuleData(FreightRuleData freightRuleData) {
        this.freightRuleData = freightRuleData;
    }
}
