package com.match.FlightRecommendation.graph;

import com.match.FlightRecommendation.data.AllData;
import com.match.FlightRecommendation.data.FlightData;

import java.util.ArrayList;
import java.util.List;

//航班图中的每条边，记录了两个城市之间的直达航班
public class FlightDataEdge {
    private List<AllData> flightDataList;

    public FlightDataEdge() {
        flightDataList = new ArrayList<>();
    }

    public List<AllData> getFlightDataList() {
        return flightDataList;
    }

    public void setFlightDataList(List<AllData> flightDataList) {
        this.flightDataList = flightDataList;
    }
}
