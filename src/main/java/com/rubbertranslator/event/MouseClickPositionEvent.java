package com.rubbertranslator.event;

import java.awt.*;

public class MouseClickPositionEvent {
    private Point point;

    public MouseClickPositionEvent() {
    }

    public MouseClickPositionEvent(Point point) {
        this.point = point;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
}
