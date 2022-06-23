package com.match.FlightRecommendation.inter;

import com.match.FlightRecommendation.bean.LowPriceRecBean;
import com.match.FlightRecommendation.bean.UpdateBean;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface LowPriceRecService {
    List<LowPriceRecBean> findLowPriceFlightCarriers(String[] time, String[] departure, String[] arrival) throws Exception;

    List<LowPriceRecBean> findLowPriceFlightPassengers(int passengerNum, String [] time, String [] departure, String [] arrival) throws Exception;

    UpdateBean updateRemainData(String sequenceNum, String [] cabins, String [] changeNum) throws Exception;

    UpdateBean insertData(String carrier, String flightNum, String departure, String arrival, String startTime, String endTime, String price) throws Exception;
}

