package com.rubbertranslator.modules.translate;

import com.rubbertranslator.modules.translate.baidu.BaiduTranslator;
import com.rubbertranslator.modules.translate.google.GoogleTranslator;
import com.rubbertranslator.modules.translate.youdao.YoudaoTranslator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/8 15:26
 * 翻译模块向外提供的接口
 */
public class TranslatorFactory {

    private final Map<TranslatorType, AbstractTranslator> translatorEngineMap = new HashMap<>();

    private TranslatorType engineType = TranslatorType.GOOGLE;

    private Language sourceLanguage = Language.AUTO;

    private Language destLanguage = Language.CHINESE_SIMPLIFIED;

    // 加载所有翻译引擎
    {
        instanceTranslatorEngine(TranslatorType.GOOGLE);
        instanceTranslatorEngine(TranslatorType.BAIDU);
        instanceTranslatorEngine(TranslatorType.YOUDAO);
    }


    public Language getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(Language sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public Language getDestLanguage() {
        return destLanguage;
    }

    public void setDestLanguage(Language destLanguage) {
        this.destLanguage = destLanguage;
    }

    public void setEngineType(TranslatorType type){
        engineType = type;
    }

    public String translate(String text){
        // lazy load
//        if(!translatorEngineMap.containsKey(engineType)){
//            instanceTranslatorEngine(engineType);
//        }
        // 首先使用当前首选的翻译引擎翻译
        String result = null;
        AbstractTranslator currentEngine = translatorEngineMap.get(engineType);
        result =  currentEngine.translate(sourceLanguage,destLanguage,text);
        if(result != null){
            return result;
        }else{
            // 一旦失败,自动使用其他翻译引擎翻译
            for(AbstractTranslator translator: translatorEngineMap.values()){
                if(translator == currentEngine){
                    continue;
                }
                result = translator.translate(sourceLanguage,destLanguage,text);
                if(result != null){
                    return result;
                }
            }
        }
        return null;
    }


    public void addTranslator(TranslatorType type, AbstractTranslator translator){
        translatorEngineMap.put(type,translator);
    }

    /**
     * 取回指定type的translator
     * @param type 翻译类型
     * @return translator
     *          null 如果没有该translator
     */
    public AbstractTranslator getTranslator(TranslatorType type){
        return translatorEngineMap.get(type);
    }

    /**
     * 用户没有主动添加翻译引擎，则采用默认设置
     * @param type
     */
    private void instanceTranslatorEngine(TranslatorType type){
        // 翻译接口多的话，可以改用反射+映射关系表
        // 但是不多，直接用switch判断即可
        switch (type){
            case BAIDU:
                translatorEngineMap.put(TranslatorType.BAIDU,new BaiduTranslator());break;
            case GOOGLE:
                translatorEngineMap.put(TranslatorType.GOOGLE,new GoogleTranslator());break;
            case YOUDAO:
                translatorEngineMap.put(TranslatorType.YOUDAO,new YoudaoTranslator());break;
        }
    }

}
