package com.match.FlightRecommendation.controller;

import com.match.FlightRecommendation.data.Base;
import com.match.FlightRecommendation.inter.LowPriceRecService;
import com.match.FlightRecommendation.service.LowPriceRec;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("test")
public class TestController {
    private LowPriceRecService lowPriceRec;

    @PostMapping("post")
    @ResponseBody
    public List<Base> request(@RequestParam("name") String name, @RequestParam("age") int age) {
        List<Base> bases = new ArrayList<>();
        bases.add(new Base(name, name, name));
        bases.add(new Base(name, name, name));
        return bases;
    }
}
