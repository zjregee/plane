package com.match.FlightRecommendation.controller;

import com.match.FlightRecommendation.bean.PathBean;
import com.match.FlightRecommendation.bean.UpdateBean;
import com.match.FlightRecommendation.inter.LowPriceRecService;
import com.match.FlightRecommendation.service.LowPriceRec;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CrossOrigin()
@RestController
@RequestMapping("recommend")
public class FlightRec {
    private final LowPriceRecService service = new LowPriceRec();

    @PostMapping("first")
    public CompletableFuture<List<PathBean>> findFlight(@RequestParam("times") String times, @RequestParam("departures") String departures, @RequestParam("arrival") String arrivals) throws Exception {
        return service.findLowPriceFlightPassengers(1, times, departures, arrivals);
    }

    @PostMapping("second")
    public CompletableFuture<List<PathBean>> findFlightCarriers(@RequestParam("passengerNum") int passengerNum, @RequestParam("times") String times, @RequestParam("departures") String departures, @RequestParam("arrival") String arrivals) throws Exception {
        return service.findLowPriceFlightCarriers(passengerNum, times, departures, arrivals);
    }

    @PostMapping("third")
    public CompletableFuture<List<PathBean>> findLowPriceFlightPassengers(@RequestParam("num") String num, @RequestParam("times") String times, @RequestParam("departures") String departures, @RequestParam("arrival") String arrivals) throws Exception {
        return service.findLowPriceFlightPassengers(Integer.parseInt(num), times, departures, arrivals);
    }

    @PostMapping("update")
    public CompletableFuture<UpdateBean> updateRemainData(@RequestParam("flightNum") String flightNum, @RequestParam("cabins") String cabins, @RequestParam("changeNums") String changeNum) throws Exception {
        return service.updateRemainData(flightNum, cabins.split(","), changeNum.split(","));
    }

    @PostMapping("insert")
    public CompletableFuture<UpdateBean> insertData(@RequestParam("carrier") String carrier, @RequestParam("flightNum") String flightNum,
                                                    @RequestParam("departure") String departure, @RequestParam("arrival") String arrival,
                                                    @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
                                                    @RequestParam("price") String price) throws Exception {
        return service.insertData(carrier, flightNum, departure, arrival, startTime, endTime, price);
    }
}
