package com.rubbertranslator.mvp.view.controller;

import com.rubbertranslator.mvp.view.IView;

import java.util.List;

public interface IFilterView extends IView {
    void addFilterProcesses(List<String> processNames);

    void removeFilterProcesses(List<String> processNames);
}
