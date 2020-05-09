package com.rubbertranslator.modules.textinput.mousecopy.copymethods;

import org.jnativehook.mouse.NativeMouseEvent;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/4/27 17:35
 * 快速双击触发copy
 */
public class DoubleClickCopyMethod extends CopyMethod {

    /* 时间间隔 单位ms*/
    private long threshold;
    // 双击的计算方案不太好，健壮性不行
    private long[] clickTimeRecord = new long[2];
    private int clickTimeArrIdx = -1;


    public DoubleClickCopyMethod() {
        this.threshold = 300;    // default: 300ms
    }

    public long getThreshold() {
        return threshold;
    }

    public void setInterval(long threshold) {
        this.threshold = threshold;
    }

    @Override
    public void onPressed(NativeMouseEvent event) {
        isProcessed = false;
    }

    @Override
    public void onRelease(NativeMouseEvent event) {
        // set time arr to calculate interval for double click
        clickTimeArrIdx = (++clickTimeArrIdx) & 1;
        clickTimeRecord[clickTimeArrIdx] = System.currentTimeMillis();
        // tryCopy
        if (tryCopy()) {
            isProcessed = true;
        }
    }

    /**
     * try to copy
     *
     * @return true if copy success
     * false if copy failed
     */
    private boolean tryCopy() {
        long interval = getClickInterval();
        if (interval < threshold) {
            copyerRobot.triggerCopy();
            return true;
        } else {
            return false;
        }
    }

    /**
     * 计算双击鼠标之间的间隔
     *
     * @return
     */
    public long getClickInterval() {
        return Math.abs(clickTimeRecord[0] - clickTimeRecord[1]);
    }
}
