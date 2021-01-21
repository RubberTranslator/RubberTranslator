package com.rubbertranslator.entity;

public class WindowSize {
    private Double x;
    private Double y;

    public WindowSize(Double width, Double height) {
        this.x = width;
        this.y = height;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }
}
