package com.rubbertranslator.utils;

import com.rubbertranslator.entity.AppearanceSetting;

public class AppearanceSettingUtil {
    public static String appearanceSettingCss(AppearanceSetting setting){
        double appFontSize = setting.appFontSize;
        double textFontSize = setting.textFontSize;
        return "" +
                ".root  {\n" +      // .root
                "     -fx-font-size: " + (int)appFontSize + "pt;\n" +
                "}\n" +
                ".text-area {\n" +   // .text-area
                "     -fx-font-size: " + (int)textFontSize + "pt;\n" +
                "}";
    }
}
