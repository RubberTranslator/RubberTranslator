/**
 * @author Raven
 * @date 2020/5/9 11:12
 * @version 1.0
 */
module RubberTranslator {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.logging;
    requires jna;
    requires jna.platform;
    requires java.desktop;
    requires jnativehook;
    requires com.google.gson;
    requires okhttp3;

    exports com.rubbertranslator;
    exports com.rubbertranslator.controller;
    exports com.rubbertranslator.modules.translate;
    exports com.rubbertranslator.modules.translate.baidu;
    exports com.rubbertranslator.modules.translate.youdao;
    exports com.rubbertranslator.modules.translate.google;
    exports com.rubbertranslator.modules.textinput.ocr;
    exports com.rubbertranslator.modules.system;

    opens com.rubbertranslator.controller;
    opens com.rubbertranslator.modules.translate.baidu;
    opens com.rubbertranslator.modules.translate.youdao;
    opens com.rubbertranslator.modules.translate.google;
    opens com.rubbertranslator.modules.textinput.ocr;
    opens com.rubbertranslator.modules.system;
}