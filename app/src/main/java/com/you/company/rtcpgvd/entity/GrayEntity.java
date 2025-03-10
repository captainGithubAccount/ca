package com.you.company.rtcpgvd.entity;

public class GrayEntity {

    private String sampleArea;

    private String grayVal;

    public GrayEntity(String sampleArea, String grayVal) {
        this.sampleArea = sampleArea;
        this.grayVal = grayVal;
    }

    public String getSampleArea() {
        return sampleArea;
    }

    public void setSampleArea(String sampleArea) {
        this.sampleArea = sampleArea;
    }

    public String getGrayVal() {
        return grayVal;
    }

    public void setGrayVal(String grayVal) {
        this.grayVal = grayVal;
    }

    @Override
    public String toString() {
        return "{" + "zone " + sampleArea  + " val: " + grayVal + "}";
    }
}
