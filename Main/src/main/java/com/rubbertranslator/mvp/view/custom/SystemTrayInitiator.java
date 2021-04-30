package com.rubbertranslator.mvp.view.custom;

import com.rubbertranslator.event.SystemTrayClickEvent;
import org.greenrobot.eventbus.EventBus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/6 13:02
 */
public class SystemTrayInitiator {
    /**
     * 无状态Item
     */
    private static final MenuItem showWindowItem =  new MenuItem("主窗口");

    private static final MenuItem exitItem = new MenuItem("退出");

    public static void initialize() {
        // 初始化系统托盘
        createSystemTray();
    }

    /*
     * 添加系统托盘
     */
    private static void createSystemTray() {
        if (SystemTray.isSupported()) {
            // 获取当前平台的系统托盘
            SystemTray tray = SystemTray.getSystemTray();
            // 加载一个图片用于托盘图标的显示
            Image image = Toolkit.getDefaultToolkit().getImage(
                    SystemTrayInitiator.class.getClassLoader().getResource("pic/logo.png"));
            PopupMenu popupMenu = createPopupMenuItems();
            TrayIcon trayIcon = new TrayIcon(image, "RubberTranslator", popupMenu);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(e.getButton() == MouseEvent.BUTTON1){
                        // Show Main Window
                        EventBus.getDefault().post(new SystemTrayClickEvent(SystemTrayClickEvent.SHOW_MAIN_WINDOW));
                    }
                }
            });
            // 托盘图标自适应尺寸
            trayIcon.setImageAutoSize(true);
            // 添加托盘图标到系统托盘
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            Logger.getLogger(SystemTrayInitiator.class.getName()).info("当前系统不支持系统托盘");
        }
    }

    /**
     * 创建点击图标时的弹出菜单
     *
     * @return
     */
    private static PopupMenu createPopupMenuItems() {
        PopupMenu popupMenu = new PopupMenu();
//        java.awt.Font defaultFont = java.awt.Font.decode(null); // default font
//        float adjustmentRatio = (float) (Toolkit.getDefaultToolkit().getScreenResolution()/96.0); //  Calculate this based on your metrics
//        float newFontSize = defaultFont.getSize() * adjustmentRatio ;
//        java.awt.Font derivedFont = defaultFont.deriveFont(newFontSize);
//        showWindowItem.setFont(derivedFont);
//        exitItem.setFont(derivedFont);
        // 事件处理
        SystemTrayEventProcess systemTrayPopupEventProcess = new SystemTrayEventProcess();
        showWindowItem.addActionListener(systemTrayPopupEventProcess);
        exitItem.addActionListener(systemTrayPopupEventProcess);
        popupMenu.add(showWindowItem);
        popupMenu.add(exitItem);
        return popupMenu;
    }


    /**
     * 系统托盘menuitem事件处理类
     */
    private static class SystemTrayEventProcess implements ActionListener {

        /**
         * 无状态item 处理
         * @param e
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if(source == exitItem){
                // 是否需要做一些回收工作？
                EventBus.getDefault().post(new SystemTrayClickEvent(SystemTrayClickEvent.EXIT));
            }else if(source == showWindowItem){
                EventBus.getDefault().post(new SystemTrayClickEvent(SystemTrayClickEvent.SHOW_MAIN_WINDOW));
            }
        }


    }

}
