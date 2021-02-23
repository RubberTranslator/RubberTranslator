package com.rubbertranslator.mvp.presenter.impl;

import com.rubbertranslator.enumtype.HistoryEntryIndex;
import com.rubbertranslator.enumtype.RecordModeType;
import com.rubbertranslator.mvp.modules.history.HistoryEntry;
import com.rubbertranslator.mvp.view.IRecordView;
import com.rubbertranslator.system.ProgramPaths;
import com.rubbertranslator.utils.OSTypeUtil;

import java.util.List;

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


    private String exportDir;


    /**
     * 更新记录导出路径
     */
    private void updateExportPath() {
        exportDir = ProgramPaths.exportDir;
        if (OSTypeUtil.isWin()) {
            exportDir = exportDir.replaceAll("\\\\", "/");
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


    public void setRecordModeType(List<RecordModeType> recordMode) {
        translatorFacade.getHistory().setRecordModeTypes(recordMode);
    }

    public void correctCurrentEntry(String originText, String translateText) {
        boolean ret = translatorFacade.getHistory().modifyCurrentEntry(new HistoryEntry(originText, translateText));
        if (ret) {
            view.correctCallBack("修正成功");
        }else{
            view.correctCallBack("修正失败(检查当前记录是否为空)");
        }
    }

    public void deleteCurrentEntry() {
        if (translatorFacade.getHistory().getHistorySize() == 0) {
            view.setText("当前历史记录为空", "");
        } else {
            translatorFacade.getHistory().deleteCurrentEntry();
            setHistoryEntry(HistoryEntryIndex.CURRENT_HISTORY);
            updateHistoryNumDisplay();
        }
    }

    public void record(boolean isStart) {
        if (isStart) {
            updateExportPath();
            translatorFacade.getHistory().startRecord(exportDir);
            view.recordStart(exportDir);
        } else {
            // TODO: 完成导出
            translatorFacade.getHistory().endRecord();
            view.recordEnd(exportDir);
        }
    }
}
