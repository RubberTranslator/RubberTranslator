package com.rubbertranslator.event;

public class OpacityValueChangeEvent {
    private double value;

    public OpacityValueChangeEvent(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
