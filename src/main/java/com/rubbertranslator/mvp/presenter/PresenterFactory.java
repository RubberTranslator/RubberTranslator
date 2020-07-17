package com.rubbertranslator.mvp.presenter;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.mvp.presenter.impl.FilterViewPresenter;
import com.rubbertranslator.mvp.presenter.impl.FocusViewPresenter;
import com.rubbertranslator.mvp.presenter.impl.MainViewPresenter;
import com.rubbertranslator.mvp.presenter.impl.WordsReplacerPresenter;

import java.util.HashMap;
import java.util.Map;

public class PresenterFactory {

    private static final Map<SceneType,ConfigPresenter> presenterMap = new HashMap<>();

    public static ConfigPresenter getPresenter(SceneType type){
        ConfigPresenter presenter;
        if(!presenterMap.containsKey(type)){
            switch(type){
                case MAIN_SCENE:
                    presenter = new MainViewPresenter();
                    presenterMap.put(SceneType.MAIN_SCENE,presenter);
                case FOCUS_SCENE:
                    presenter = new FocusViewPresenter();
                    presenterMap.put(SceneType.FOCUS_SCENE,presenter);
                case FILTER_SCENE:
                    presenter = new FilterViewPresenter();
                    presenterMap.put(SceneType.FILTER_SCENE,presenter);
                case WORDS_REPLACE_SCENE:
                    presenter = new WordsReplacerPresenter();
                    presenterMap.put(SceneType.WORDS_REPLACE_SCENE,presenter);
                default:
                    break;
            }
        }
        return presenterMap.get(type);
    }
}
