package com.example.varga.steps;

import java.util.Date;

public class Steps {
    private String userID;
    private int stepsNum;
    private Date currentdate;

    public Steps(String userID, int stepsNum, Date currentdate) {
        this.userID = userID;
        this.stepsNum = stepsNum;
        this.currentdate = currentdate;
    }

    public Steps() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getStepsNum() {
        return stepsNum;
    }

    public void setStepsNum(int stepsNum) {
        this.stepsNum = stepsNum;
    }

    public Date getCurrentdate() {
        return currentdate;
    }

    public void setCurrentdate(Date currentdate) {
        this.currentdate = currentdate;
    }
}
