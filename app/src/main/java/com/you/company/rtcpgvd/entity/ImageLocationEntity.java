package com.you.company.rtcpgvd.entity;

import com.you.company.rtcpgvd.RegionType;

public class ImageLocationEntity {

    private int matrixId;

    private float startX;

    private float startY;

    private float endX;

    private float endY;

    private RegionType type;

    public ImageLocationEntity(int matrixId, float startX, float startY, float endX, float endY, RegionType type) {
        this.matrixId = matrixId;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.type = type;
    }

    public int getMatrixId() {
        return matrixId;
    }

    public void setMatrixId(int matrixId) {
        this.matrixId = matrixId;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public RegionType getType() {
        return type;
    }

    public void setType(RegionType type) {
        this.type = type;
    }
}
