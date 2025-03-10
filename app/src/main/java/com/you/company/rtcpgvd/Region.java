package com.you.company.rtcpgvd;

public class Region {

    private float startX;

    private float startY;

    private float endX;

    private float endY;

    private RegionType regionType;

    public int getMatrixId() {
        return matrixId;
    }

    public void setMatrixId(int matrixId) {
        this.matrixId = matrixId;
    }

    private int matrixId;

    public Region(float startX, float startY, float endX, float enY,int matrixId,RegionType type) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = enY;
        this.matrixId = matrixId;
        this.regionType = type;
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

    public void setEndY(float enY) {
        this.endY = enY;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }
}
