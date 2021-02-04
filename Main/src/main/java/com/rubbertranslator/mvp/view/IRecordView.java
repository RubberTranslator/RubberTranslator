package com.rubbertranslator.mvp.view;

import com.rubbertranslator.mvp.view.controller.ISingleTranslateView;

/**
 * @author Raven
 * @version 1.0
 * date  2021/2/4 9:16
 */
public interface IRecordView extends ISingleTranslateView {
    void recordStart(String recordPath);

    void recordEnd(String recordPath);
}
