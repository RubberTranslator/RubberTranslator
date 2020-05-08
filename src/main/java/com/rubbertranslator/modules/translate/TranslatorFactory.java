package com.rubbertranslator.modules.translate;

import com.rubbertranslator.modules.translate.baidu.BaiduTranslator;
import com.rubbertranslator.modules.translate.google.GoogleTranslator;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/8 15:26
 * 翻译模块向外提供的接口
 */
public class TranslatorFactory {

    private final Map<TranslatorType, ITranslator> translatorEngineMap = new HashMap<>();

    private TranslatorType engineType = TranslatorType.GOOGLE;


    public void setEngineType(TranslatorType type){
        engineType = type;
    }

    public String translate(String source, String dest, String text){
        if(!translatorEngineMap.containsKey(engineType)){
            instanceTranslatorEngine(engineType);
        }
        ITranslator translatorEngine = translatorEngineMap.get(engineType);
        return translatorEngine.translate(source,dest,text);
    }

    private void instanceTranslatorEngine(TranslatorType type){
        // 翻译接口多的话，可以改用反射+映射关系表
        // 但是不多，直接用switch判断即可
        switch (type){
            case BAIDU:
                translatorEngineMap.put(TranslatorType.BAIDU,new BaiduTranslator());break;
            case GOOGLE:
                //
                translatorEngineMap.put(TranslatorType.GOOGLE,new GoogleTranslator());break;
        }
    }

}
