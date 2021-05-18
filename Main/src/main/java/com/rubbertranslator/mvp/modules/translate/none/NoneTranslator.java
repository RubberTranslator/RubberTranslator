package com.rubbertranslator.mvp.modules.translate.none;

import com.rubbertranslator.enumtype.Language;
import com.rubbertranslator.mvp.modules.translate.AbstractTranslator;

public class NoneTranslator extends AbstractTranslator {
    @Override
    public void addLanguageMap() {

    }

    @Override
    public String translate(Language source, Language dest, String text) {
        return text;
    }
}
