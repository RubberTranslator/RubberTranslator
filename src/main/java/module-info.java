/**
 * @author Raven
 * date  2020/5/16 19:24
 * module-info文件目前无效，因为以来了non-module的jar包，所以没办法使用jlink打包
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