package com.you.company.rtcpgvd.entity;

import java.util.ArrayList;
import java.util.List;

public class TimeGrayEntity implements Comparable<TimeGrayEntity>{

    private String time;

    private List<GrayEntity> grayEntityList;

    public TimeGrayEntity(){
        grayEntityList = new ArrayList<>();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<GrayEntity> getGrayEntityList() {
        return grayEntityList;
    }

    public void setGrayEntityList(List<GrayEntity> grayEntityList) {
        this.grayEntityList = grayEntityList;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("time: ");
        strBuilder.append(time);
        strBuilder.append("{ ");
        for (GrayEntity item : grayEntityList) {
            strBuilder.append(item.toString());
        }
        strBuilder.append(" }");
        strBuilder.append("\n");
        return strBuilder.toString();
    }

    @Override
    public int compareTo(TimeGrayEntity o) {
        return Integer.compare(Integer.parseInt(this.time),Integer.parseInt(o.getTime()));
    }
}
