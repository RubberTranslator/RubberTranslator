package com.rubbertranslator.mvp.modules.textinput.mousecopy.copymethods;


/**
 * @author Raven
 * @version 1.0
 * date 2020/4/28 11:51
 * 鼠标事件分发器
 */
public class MacMouseEventDispatcher extends AbstractMouseEventDispatcher {

    private final LinuxMouseEventDispatcher mouseEventDispatcher = new LinuxMouseEventDispatcher();

    @Override
    protected void initHookLibResources() {
        mouseEventDispatcher.initHookLibResources();;
    }

    @Override
    public void releaseResources() {
        mouseEventDispatcher.releaseResources();
    }


}
