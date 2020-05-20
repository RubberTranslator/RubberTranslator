/**
 * @author Raven
 * date  2020/5/16 19:24
 * IDEA 2020版目前必须要有这个文件，不模块化无法启动
 */
open module rubbertranslator {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;
    requires com.google.gson;
    requires jna;
    requires jna.platform;
    requires org.objectweb.asm;
    requires cglib;
    requires jnativehook;
    requires okhttp3;
    requires junique;
    requires eventbus;

    exports com.rubbertranslator;
    exports com.rubbertranslator.modules.textinput;
    exports com.rubbertranslator.modules.filter;
    exports com.rubbertranslator.modules.textprocessor.pre;
    exports com.rubbertranslator.modules.textprocessor.post;
    exports com.rubbertranslator.modules.translate;
    exports com.rubbertranslator.modules.history;
    exports com.rubbertranslator.modules.afterprocess;
    exports com.rubbertranslator.system;
}