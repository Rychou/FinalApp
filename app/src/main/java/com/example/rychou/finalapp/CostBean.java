package com.example.rychou.finalapp;

public class CostBean {
    private int mId;

    private String mType;
    private String mWay;
    private double mFee;
    private String mTime;
    private String mBudget;
    private String mComment;

    public CostBean(int id,String type, String way, double fee, String time,String budget, String comment){
        mId = id;
        mType = type;
        mWay = way;
        mFee = fee;
        mTime = time;
        mBudget = budget;
        mComment = comment;
    }

    public CostBean(){}
    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public double getFee() {
        return mFee;
    }

    public void setFee(double fee) {
        mFee = fee;
    }

    public String getBudget() {
        return mBudget;
    }

    public void setBudget(String budget) {
        mBudget = budget;
    }

    public String getWay() {
        return mWay;
    }

    public void setWay(String way) {
        mWay = way;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String comment) {
        mComment = comment;
    }
}
