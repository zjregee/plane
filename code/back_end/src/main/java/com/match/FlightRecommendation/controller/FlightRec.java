package com.match.FlightRecommendation.controller;

import com.match.FlightRecommendation.bean.LowPriceRecBean;
import com.match.FlightRecommendation.bean.UpdateBean;
import com.match.FlightRecommendation.inter.LowPriceRecService;
import com.match.FlightRecommendation.service.LowPriceRec;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("recommend")
public class FlightRec {
    private final LowPriceRecService service = new LowPriceRec();

    @PostMapping("first")
    public List<LowPriceRecBean> findFlight(@RequestParam("times") String times, @RequestParam("departures") String departures, @RequestParam("arrival") String arrivals) throws Exception {
        return service.findLowPriceFlightPassengers(1, times.split(","), departures.split(","), arrivals.split(","));
    }

    @PostMapping("second")
    public List<LowPriceRecBean> findFlightCarriers(@RequestParam("times") String times, @RequestParam("departures") String departures, @RequestParam("arrival") String arrivals) throws Exception {
        return service.findLowPriceFlightCarriers(times.split(","), departures.split(","), arrivals.split(","));
    }

    @PostMapping("third")
    public List<LowPriceRecBean> findLowPriceFlightPassengers(@RequestParam("num") String num, @RequestParam("times") String times, @RequestParam("departures") String departures, @RequestParam("arrival") String arrivals) throws Exception {
        return service.findLowPriceFlightPassengers(Integer.parseInt(num), times.split(","), departures.split(","), arrivals.split(","));
    }

    @PostMapping("update")
    public UpdateBean updateRemainData(@RequestParam("flightNum") String flightNum, @RequestParam("cabins") String cabins, @RequestParam("changeNums") String changeNum) throws Exception {
        return service.updateRemainData(flightNum, cabins.split(","), changeNum.split(","));
    }

    @PostMapping("insert")
    public UpdateBean insertData(@RequestParam("carrier") String carrier, @RequestParam("flightNum") String flightNum,
                                 @RequestParam("departure") String departure, @RequestParam("arrival") String arrival,
                                 @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
                                 @RequestParam("price") String price) throws Exception {
        return service.insertData(carrier, flightNum, departure, arrival, startTime, endTime, price);
    }
}
