package com.match.FlightRecommendation.bean;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ResultBean implements Serializable {
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("data")
    private String data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
