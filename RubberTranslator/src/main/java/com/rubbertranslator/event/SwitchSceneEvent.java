package com.rubbertranslator.event;

import com.rubbertranslator.enumtype.SceneType;

/**
 * @author Raven
 * @version 1.0
 * date  2020/12/4 14:04
 */
public class SwitchSceneEvent {
    private SceneType type;

    public SwitchSceneEvent(SceneType type) {
        this.type = type;
    }

    public SceneType getType(){
        return type;
    }
}
