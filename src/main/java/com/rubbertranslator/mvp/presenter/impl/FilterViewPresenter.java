package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.modules.filter.ProcessFilter;
import com.rubbertranslator.mvp.view.IFilterView;

import java.util.List;

public class FilterViewPresenter extends Presenter {

    public void addFilterList(List<String> processNames){
        ProcessFilter processFilter = clipboardListenerThread.getProcessFilter();
        processFilter.addFilterList(processNames);
        configManger.getSystemConfiguration().setProcessList(List.copyOf(processFilter.getFilterList()));
        ((IFilterView)scene).addFilterProcesses(processNames);
    }

    public void removeFilterList(List<String> processNames){
        ProcessFilter processFilter = clipboardListenerThread.getProcessFilter();
        processFilter.removeFilterLists(processNames);
        configManger.getSystemConfiguration().setProcessList(List.copyOf(processFilter.getFilterList()));
        ((IFilterView)scene).removeFilterProcesses(processNames);

    }

    public void setOpenProcessFilter(boolean isOpen)
    {
        configManger.getSystemConfiguration().setOpenProcessFilter(isOpen);
        clipboardListenerThread.getProcessFilter().setOpen(isOpen);

    }
}
