package com.match.FlightRecommendation.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KeyBean implements Serializable {

    @JsonProperty("key")
    private String key;

    public KeyBean(String key) {
        this.key = key;
    }
}
