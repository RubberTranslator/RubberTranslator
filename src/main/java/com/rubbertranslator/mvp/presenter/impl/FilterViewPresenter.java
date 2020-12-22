package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.mvp.modules.filter.ProcessFilter;
import com.rubbertranslator.mvp.presenter.ModelPresenter;
import com.rubbertranslator.mvp.view.controller.IFilterView;

import java.util.List;

public class FilterViewPresenter extends ModelPresenter<IFilterView> {

    public void addFilterList(List<String> processNames){
        ProcessFilter processFilter = clipboardListenerThread.getProcessFilter();
        processFilter.addFilterList(processNames);
        configManger.getSystemConfiguration().setProcessList(List.copyOf(processFilter.getFilterList()));
        view.addFilterProcesses(processNames);
    }

    public void removeFilterList(List<String> processNames){
        ProcessFilter processFilter = clipboardListenerThread.getProcessFilter();
        processFilter.removeFilterLists(processNames);
        configManger.getSystemConfiguration().setProcessList(List.copyOf(processFilter.getFilterList()));
        view.removeFilterProcesses(processNames);
    }

    public void setOpenProcessFilter(boolean isOpen)
    {
        configManger.getSystemConfiguration().setOpenProcessFilter(isOpen);
        clipboardListenerThread.getProcessFilter().setOpen(isOpen);
    }
}
