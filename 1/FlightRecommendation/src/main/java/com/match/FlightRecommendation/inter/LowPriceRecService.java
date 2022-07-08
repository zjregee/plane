package com.match.FlightRecommendation.inter;

import com.match.FlightRecommendation.bean.PathBean;
import com.match.FlightRecommendation.bean.UpdateBean;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LowPriceRecService {
    CompletableFuture<List<PathBean>> findLowPriceFlightCarriers(int passengerNum, String time, String departure, String arrival);

    CompletableFuture<List<PathBean>> findLowPriceFlightPassengers(int passengerNum, String time, String departure, String arrival);

    CompletableFuture<UpdateBean> updateRemainData(String sequenceNum, String [] cabins, String [] changeNum) throws Exception;

    CompletableFuture<UpdateBean> insertData(String carrier, String flightNum, String departure, String arrival, String startTime, String endTime, String price) throws Exception;
}

