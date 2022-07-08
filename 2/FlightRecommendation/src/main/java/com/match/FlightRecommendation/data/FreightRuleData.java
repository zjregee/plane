package com.match.FlightRecommendation.data;

import java.io.Serializable;
import java.util.List;

public class FreightRuleData extends Base implements Serializable {

    private String sequenceNo;  // 序号，8位

    private String nextCarrier; // 后续衔接航班承运人，2位

    private List<String> agencies;  // 允许出票的代理人队列，60位

    private int surcharge;  // 额外费用占比，范围:[-1,100]

    public FreightRuleData(String sequenceNo, String nextCarrier, List<String> agencies, int surcharge) {
        this.sequenceNo = sequenceNo;
        this.nextCarrier = nextCarrier;
        this.agencies = agencies;
        this.surcharge = surcharge;
    }

    public FreightRuleData() {

    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public String getNextCarrier() {
        return nextCarrier;
    }

    public void setNextCarrier(String nextCarrier) {
        this.nextCarrier = nextCarrier;
    }

    public List<String> getAgencies() {
        return agencies;
    }

    public void setAgencies(List<String> agencies) {
        this.agencies = agencies;
    }

    public int getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(int surcharge) {
        this.surcharge = surcharge;
    }
}
