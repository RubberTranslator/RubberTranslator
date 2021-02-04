package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.HistoryEntryIndex;
import com.rubbertranslator.enumtype.RecordModeType;
import com.rubbertranslator.mvp.modules.history.HistoryEntry;
import com.rubbertranslator.mvp.view.IRecordView;
import com.rubbertranslator.utils.OSTypeUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Raven
 * @version 1.0
 * date  2021/2/4 9:29
 */
public class RecordViewPresenter extends SingleTranslatePresenter<IRecordView> {

    // 无法使用的功能配置相关
    private boolean oldAutoPaste = false;

    private boolean oldAutoCopy = false;

    private boolean oldIncrementCopy = false;

    // 记录导出相关
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private String exportPath;


    /**
     * 更新记录导出路径
     */
    private void updateExportPath() {
        exportPath = System.getProperty("user.dir") + "/RubberTranslator/export/记录" + sdf.format(new Date()) +
                ".txt";
        if (OSTypeUtil.isWin()) {
            exportPath = exportPath.replaceAll("\\\\", "/");
        }
    }

    @Override
    public void setHistoryEntry(HistoryEntryIndex index) {
        super.setHistoryEntry(index);
        updateHistoryNumDisplay();
    }

    @Override
    public void translateEndHook() {
        super.translateEndHook();
        updateHistoryNumDisplay();
    }

    private void updateHistoryNumDisplay() {
        int cur = translatorFacade.getHistory().getCurrentCursor();
        int total = translatorFacade.getHistory().getHistorySize();
        view.setHistoryNum(cur, total);
    }

    public void clearHistoryConfig() {
        translatorFacade.getHistory().endRecord();
    }

    public void closeAndSaveAutoCopyPaste() {
        oldAutoCopy = configManger.getSystemConfiguration().isAutoCopy();
        oldAutoPaste = configManger.getSystemConfiguration().isAutoPaste();
        oldIncrementCopy = configManger.getSystemConfiguration().isIncrementalCopy();
        translatorFacade.getAfterProcessor().setAutoCopy(false);
        translatorFacade.getAfterProcessor().setAutoPaste(false);
        translatorFacade.getTextPreProcessor().setIncrementalCopy(false);
    }

    public void restoreAutoCopyPasteConfig() {
        configManger.getSystemConfiguration().setAutoCopy(oldAutoCopy);
        configManger.getSystemConfiguration().setAutoPaste(oldAutoPaste);
        configManger.getSystemConfiguration().setIncrementalCopy(oldIncrementCopy);
        translatorFacade.getAfterProcessor().setAutoCopy(oldAutoCopy);
        translatorFacade.getAfterProcessor().setAutoPaste(oldAutoPaste);
        translatorFacade.getTextPreProcessor().setIncrementalCopy(oldIncrementCopy);
    }


    public void setRecordModeType(RecordModeType recordMode) {
        translatorFacade.getHistory().setRecordModeType(recordMode);
    }

    public void correctCurrentEntry(String originText, String translateText) {
        translatorFacade.getHistory().modifyCurrentEntry(new HistoryEntry(originText, translateText));
        view.correctCallBack();
    }

    public void record(boolean isStart) {
        if (isStart) {
            updateExportPath();
            translatorFacade.getHistory().startRecord(exportPath);
            view.recordStart(exportPath);
        } else {
            // TODO: 完成导出
            translatorFacade.getHistory().endRecord();
            view.recordEnd(exportPath);
        }
    }
}
