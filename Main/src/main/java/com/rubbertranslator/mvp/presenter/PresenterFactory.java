package com.rubbertranslator.mvp.presenter;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.mvp.presenter.impl.*;

import java.util.HashMap;
import java.util.Map;

public class PresenterFactory {

    private static final Map<SceneType, BasePresenter> presenterMap = new HashMap<>();

    public static <T extends BasePresenter> T getPresenter(SceneType type){
        T presenter;
        if(!presenterMap.containsKey(type)){
            switch(type){
                case MAIN_SCENE:
                    presenter = (T) new MainViewPresenter();
                    presenterMap.put(SceneType.MAIN_SCENE,presenter);
                    break;
                case FOCUS_SCENE:
                    presenter = (T) new FocusViewPresenter();
                    presenterMap.put(SceneType.FOCUS_SCENE,presenter);
                    break;
                case COMPARE_SCENE:
                    presenter = (T) new MultiTranslatePresenter();
                    presenterMap.put(SceneType.COMPARE_SCENE,presenter);
                    break;
                case FILTER_SCENE:
                    presenter = (T) new FilterViewPresenter();
                    presenterMap.put(SceneType.FILTER_SCENE,presenter);
                    break;
                case WORDS_REPLACE_SCENE:
                    presenter = (T) new WordsReplacerPresenter();
                    presenterMap.put(SceneType.WORDS_REPLACE_SCENE,presenter);
                    break;
                default:
                    break;
            }
        }
        return (T) presenterMap.get(type);
    }
}
