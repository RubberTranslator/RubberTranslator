package com.rubbertranslator.mvp.modules.history;

import com.rubbertranslator.enumtype.RecordModeType;
import com.rubbertranslator.system.SystemResourceManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

    private List<RecordModeType> recordModeTypes;

    // 记录导出相关
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private String exportPath = null;

    public TranslationHistory() {
        historyList = new ArrayList<>();
    }

    public void startRecord(String exportDir) {
        if (!isRecording) {
            isRecording = true;
            historyList.clear();
            File exportDirFile = new File(exportDir);
            // dir/记录-yyyy-MM-dd-HH-mm-ss
            exportPath = exportDirFile.getAbsoluteFile() + File.separator + "记录-" + sdf.format(new Date());
        }
    }

    public void setRecordModeTypes(List<RecordModeType> recordModeTypes) {
        this.recordModeTypes = recordModeTypes;
    }

    public void endRecord() {
        if (isRecording) {
            isRecording = false;
            saveHistory();
        }
    }

    public void addHistory(String origin, String translation) {
        if (translation == null) return;
        historyList.add(new HistoryEntry(origin, translation));
        historyCursor = historyList.size() - 1;
        if (isRecording && historyList.size() % 5 == 0) {        // 每5条写一次
            saveHistory();
        }
    }

    public boolean modifyCurrentEntry(HistoryEntry entry) {
        if (historyCursor >= 0 && historyCursor < historyList.size()) {
            historyList.set(historyCursor, entry);
            return true;
        }
        return false;
    }

    public int getCurrentCursor() {
        return historyCursor + 1;
    }

    public int getHistorySize() {
        return historyList.size();
    }

    public void deleteCurrentEntry() {
        if (historyCursor >= 0 && historyCursor < historyList.size()) {
            historyList.remove(historyCursor);
            historyCursor--;
        }
    }

    public HistoryEntry current() {
        return historyCursor >= 0 && historyCursor < historyList.size() ?
                historyList.get(historyCursor) : new HistoryEntry("", "");
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
        if (recordModeTypes == null) return;
        for (RecordModeType type : recordModeTypes) {
            SystemResourceManager.getExecutor().execute(() -> {
                        BufferedWriter bw = null;
                        try {
                            String fileNameSuffix = "";
                            if (type == RecordModeType.ORIGIN_RECORD_MODE) {
                                fileNameSuffix = "原文";
                            } else if (type == RecordModeType.TRANSLATE_RECORD_MODE) {
                                fileNameSuffix = "译文";
                            } else if (type == RecordModeType.BILINGUAL_RECORD_MODE) {
                                fileNameSuffix = "双语";
                            }

                            File exportFile = null;
                            String filePath = exportPath + "-" + fileNameSuffix + ".txt";
                            exportFile = new File(filePath);

                            if (!exportFile.getParentFile().exists()) exportFile.getParentFile().mkdirs();
                            if (!exportFile.exists()) exportFile.createNewFile();
                            bw = new BufferedWriter(new FileWriter(exportFile));

                            for (HistoryEntry entry : historyList) {
                                String line = combineHistoryEntry(entry, type);
                                bw.write(line + "\n\n");
                            }
                        } catch (IOException e) {
                            Logger.getLogger(this.getClass().getName()).severe(e.getLocalizedMessage());
                        } finally {
                            try {
                                if (bw != null) bw.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
            );
        }
    }

    private String combineHistoryEntry(HistoryEntry entry, RecordModeType type) {
        switch (type) {
            case ORIGIN_RECORD_MODE:
                return entry.getOrigin().replaceAll("[\r\t\n]", "");
            case TRANSLATE_RECORD_MODE:
                return entry.getTranslation().replaceAll("[\r\t\n]", "");
            case BILINGUAL_RECORD_MODE:
                return entry.getOrigin().replaceAll("[\r\t\n]", "") + "\n" + entry.getTranslation().replaceAll("[\r\t\n]", "");
            default:
                return "\n";
        }
    }
}
