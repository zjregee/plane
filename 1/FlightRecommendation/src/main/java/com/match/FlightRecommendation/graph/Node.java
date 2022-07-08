package com.match.FlightRecommendation.graph;

public class Node {
    private String source;

    private String target;

    private String sequenceNum;

    private String time;

    private String endTime;

    public Node(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public Node(String source, String target, String sequenceNum, String time, String endTime) {
        this.source = source;
        this.target = target;
        this.sequenceNum = sequenceNum;
        this.time = time;
        this.endTime = endTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(String sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
