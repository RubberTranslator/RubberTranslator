package com.rubbertranslator.mvp.modules.history;

import com.rubbertranslator.enumtype.RecordModeType;
import com.rubbertranslator.system.SystemResourceManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/9 10:28
 * 翻译历史记录
 */
public class TranslationHistory {

    private int historyCursor = -1;

    private volatile ArrayList<HistoryEntry> historyList;

    // 记录模式相关
    private boolean isRecording = false;

    private boolean isModified = false;

    private RecordModeType recordModeType = RecordModeType.TRANSLATE_RECORD_MODE;

    private String exportPath = null;

    public void startRecord(String exportPath) {
        if (!isRecording) {
            isRecording = true;
            this.exportPath = exportPath;
            historyList.clear();
        }
    }

    public void setRecordModeType(RecordModeType recordModeType) {
        this.recordModeType = recordModeType;
    }

    public void endRecord() {
        if (isRecording) {
            isRecording = false;
            saveHistory();
        }
    }

    public TranslationHistory() {
        historyList = new ArrayList<>();
    }


    public void addHistory(String origin, String translation) {
        if (translation == null) return;
        historyList.add(new HistoryEntry(origin, translation));
        historyCursor = historyList.size() - 1;
        if (isRecording && historyList.size() % 10 == 0) {        // 每10条写一次
            saveHistory();
        }
    }

    public void modifyCurrentEntry(HistoryEntry entry) {
        if(historyCursor >= 0 && historyCursor < historyList.size()){
            isModified = true;
            historyList.set(historyCursor, entry);
        }
    }

    public int getCurrentCursor(){
       return historyCursor+1;
    }

    public int getHistorySize(){
       return historyList.size();
    }

    public HistoryEntry current() {
        return historyCursor >= 0 && historyCursor < historyList.size() ?
                historyList.get(historyCursor) : null;
    }

    public HistoryEntry previous() {
        if (historyList.size() == 0) return new HistoryEntry("", "");
        historyCursor = Math.min(historyList.size() - 1, Math.max(historyCursor - 1, 0));
        return historyList.get(historyCursor);
    }

    public HistoryEntry next() {
        if (historyList.size() == 0) return new HistoryEntry("", "");
        historyCursor = Math.max(0, Math.min(historyCursor + 1, historyList.size() - 1));
        return historyList.get(historyCursor);
    }

    private void saveHistory() {
        System.out.println("save to: " + exportPath);
        SystemResourceManager.getExecutor().execute(() -> {
                    BufferedWriter bw = null;
                    try {
                        File exportFile = new File(exportPath);
                        if (!exportFile.getParentFile().exists()) exportFile.getParentFile().mkdirs();
                        if (!exportFile.exists()) exportFile.createNewFile();
                        bw = new BufferedWriter(new FileWriter(exportFile,!isModified));

                        for (HistoryEntry entry : historyList) {
                            String line = combineHistoryEntry(entry);
                            bw.write(line + "\n\n");
                        }
                    } catch (IOException e) {
                        Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
                    } finally {
                        try {
                            if (bw != null) bw.close();
                        } catch (IOException e) {
                        }
                        isModified = false;
                    }
                }
        );
    }

    private String combineHistoryEntry(HistoryEntry entry) {
        switch (recordModeType) {
            case ORIGIN_RECORD_MODE:
                return entry.getOrigin().replaceAll("[\t\n]","");
            case TRANSLATE_RECORD_MODE:
                return entry.getTranslation().replaceAll("[\t\n]","");
            case BILINGUAL_RECORD_MODE:
                return entry.getOrigin() .replaceAll("[\t\n]","")+ "\n" + entry.getTranslation().replaceAll("[\t\n]","");
            default:
                return "\n";
        }
    }
}
