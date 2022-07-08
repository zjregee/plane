package com.match.FlightRecommendation.bean;

import java.util.List;

public class PathBean implements Comparable<PathBean> {
    private List<LowPriceRecBean> path;
    private String totalStartTime;
    private String totalEndTime;
    private int price = 0;

    public PathBean(List<LowPriceRecBean> path) {
        this.path = path;
        for (int i = 0; i < path.size(); i++) {
            LowPriceRecBean lowPriceRecBean = path.get(i);
            price += Integer.parseInt(lowPriceRecBean.getPrice());
            if (i == 0) {
                totalStartTime = lowPriceRecBean.getStartTime();
            }
        }
        totalEndTime = path.get(path.size() - 1).getEndTime();
    }

    @Override
    public int compareTo(PathBean o) {
        return Integer.compare(price, o.getPrice());
    }

    public List<LowPriceRecBean> getPath() {
        return path;
    }

    public void setPath(List<LowPriceRecBean> path) {
        this.path = path;
    }

    public int getPrice() {
        return price;
    }

    public String getTotalStartTime() {
        return totalStartTime;
    }

    public String getTotalEndTime() {
        return totalEndTime;
    }

    public void setTotalStartTime(String totalStartTime) {
        this.totalStartTime = totalStartTime;
    }

    public void setTotalEndTime(String totalEndTime) {
        this.totalEndTime = totalEndTime;
    }
}
