package com.rubbertranslator.mvp.view.controller;

import java.util.List;

public interface IFilterView extends ISceneView {
    void addFilterProcesses(List<String> processNames);

    void removeFilterProcesses(List<String> processNames);
}
