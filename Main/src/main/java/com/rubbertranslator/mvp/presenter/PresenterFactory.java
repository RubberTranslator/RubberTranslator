package com.rubbertranslator.mvp.presenter;

import com.rubbertranslator.enumtype.SceneType;
import com.rubbertranslator.mvp.presenter.impl.*;

import java.util.HashMap;
import java.util.Map;

public class PresenterFactory {

    private static final Map<SceneType, BasePresenter> presenterMap = new HashMap<>();

    public static <T extends BasePresenter> T getPresenter(SceneType type) {
        BasePresenter presenter;
        if (!presenterMap.containsKey(type)) {
            switch (type) {
                case MAIN_SCENE:
                    presenter = new MainViewPresenter();
                    presenterMap.put(SceneType.MAIN_SCENE, presenter);
                    break;
                case FOCUS_SCENE:
                    presenter = new FocusViewPresenter();
                    presenterMap.put(SceneType.FOCUS_SCENE, presenter);
                    break;
                case COMPARE_SCENE:
                    presenter = new MultiTranslatePresenter();
                    presenterMap.put(SceneType.COMPARE_SCENE, presenter);
                    break;
                case RECORD_SCENE:
                    presenter = new RecordViewPresenter();
                    presenterMap.put(SceneType.RECORD_SCENE, presenter);
                    break;
                case FILTER_SCENE:
                    presenter = new FilterViewPresenter();
                    presenterMap.put(SceneType.FILTER_SCENE, presenter);
                    break;
                case WORDS_REPLACE_SCENE:
                    presenter = new WordsReplacerPresenter();
                    presenterMap.put(SceneType.WORDS_REPLACE_SCENE, presenter);
                    break;
                default:
                    break;
            }
        }
        return (T) presenterMap.get(type);
    }
}
