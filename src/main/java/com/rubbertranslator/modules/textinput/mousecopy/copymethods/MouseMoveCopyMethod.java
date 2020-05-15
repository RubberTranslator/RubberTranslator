package com.rubbertranslator.modules.textinput.mousecopy.copymethods;

import org.jnativehook.mouse.NativeMouseEvent;

import java.awt.*;

/**
 * @author Raven
 * @version 1.0
 * date 2020/4/27 17:37
 * 鼠标移动较长距离来触发copy
 */
public class MouseMoveCopyMethod extends CopyMethod {

    /* the threshold of position delta for trigger copy action*/
    private double threshold;
    /* 按下鼠标坐标和释放鼠标坐标 */
    private Point startPoint, endPoint;

    public MouseMoveCopyMethod() {
        this.threshold = 50;    // default: 50 px
        startPoint = endPoint = new Point(0, 0);
    }

    public double getThreshold() {
        return threshold;
    }

    public void setInterval(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public void onPressed(NativeMouseEvent event) {
        startPoint = event.getPoint();
        isProcessed = false;
    }

    @Override
    public void onRelease(NativeMouseEvent event) {
        endPoint = event.getPoint();
        if(tryCopy()){
            isProcessed = true;
        }
    }

    /**
     * try to copy
     * @return true if copy success
     *         false if copy failed
     */
    private boolean tryCopy(){
        double interval = calculateInterval();
        if(interval > threshold){
            triggerCopy();
            return true;
        }else{
            return false;
        }
    }

    private void triggerCopy(){
        copyerRobot.triggerCopy();
    }

    /**
     * 计算鼠标在点按时的移动距离
     * @return
     */
    private double calculateInterval(){
        double sx = startPoint.getX();
        double sy = startPoint.getY();
        double ex = endPoint.getX();
        double ey = endPoint.getY();
        return Math.sqrt((sx - ex)*(sx - ex) + (sy - ey)*(sy - ey));
    }

}
