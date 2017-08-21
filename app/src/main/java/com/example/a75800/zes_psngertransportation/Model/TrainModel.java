package com.example.a75800.zes_psngertransportation.Model;

/**
 * Created by 75800 on 2017/8/20.
 */

public class TrainModel implements Comparable<TrainModel> {
    public String trainNum;
    public String startDestination;
    public String arrivingTime;
    public String leavingTime;
    public String station;

    public String  getLeavingTime() {
        return leavingTime;
    }
    public void setLeavingTime(String leavingTime) {
        this.leavingTime = leavingTime;
    }

    public int compareTo(TrainModel arg0) {
        return this.getLeavingTime().compareTo(arg0.getLeavingTime());
    }
}
