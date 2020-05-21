package com.rubbertranslator.modules.config.proxy;

import com.rubbertranslator.modules.config.SystemConfiguration;
import com.rubbertranslator.system.SystemResourceManager;
import com.rubbertranslator.modules.textinput.ocr.OCRUtils;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/11 15:55
 * 静态代理
 */
public class TextInputConfigStaticProxy extends SystemConfiguration.TextInputConfig {

    private SystemConfiguration.TextInputConfig textInputConfig;



    public SystemConfiguration.TextInputConfig getTextInputConfig(){
        return textInputConfig;
    }


    @Override
    public Boolean isOpenClipboardListener() {
        return textInputConfig.isOpenClipboardListener();
    }

    @Override
    public Boolean isDragCopy() {
        return textInputConfig.isDragCopy();
    }

    @Override
    public String getBaiduOcrApiKey() {
        return textInputConfig.getBaiduOcrApiKey();
    }

    @Override
    public String getBaiduOcrSecretKey() {
        return textInputConfig.getBaiduOcrSecretKey();
    }

    public TextInputConfigStaticProxy(SystemConfiguration.TextInputConfig textInputConfig) {
        this.textInputConfig = textInputConfig;
    }

    public void setOpenClipboardListener(Boolean openClipboardListener) {
        textInputConfig.setOpenClipboardListener(openClipboardListener);
        SystemResourceManager.getClipboardListenerThread().setRun(openClipboardListener);
    }
    public void setDragCopy(Boolean dragCopy) {
        textInputConfig.setDragCopy(dragCopy);
        SystemResourceManager.getDragCopyThread().setRun(dragCopy);
    }
    public void setBaiduOcrApiKey(String baiduOcrApiKey) {
        textInputConfig.setBaiduOcrApiKey(baiduOcrApiKey);
        OCRUtils.setApiKey(baiduOcrApiKey);
    }
    public void setBaiduOcrSecretKey(String baiduOcrSecretKey) {
        textInputConfig.setBaiduOcrSecretKey(baiduOcrSecretKey);
        OCRUtils.setSecretKey(baiduOcrSecretKey);
    }
}
