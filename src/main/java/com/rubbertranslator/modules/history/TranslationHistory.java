package com.rubbertranslator.modules.history;

/**
 * @author Raven
 * @version 1.0
 * @date 2020/5/9 10:28
 * 翻译历史记录
 */
public class TranslationHistory {
    // 最大100条记录
    private final static int MAX_NUM = 100;
    private int historyCursor = -1;

    private LimitedLinkedList<HistoryEntry> historyList ;

    public TranslationHistory() {
        this(2);
    }

    public TranslationHistory(int capacity) {
      historyList = new LimitedLinkedList<>(capacity);
    }

    public LimitedLinkedList<HistoryEntry> getHistoryList() {
        return historyList;
    }

    public void addHistory(String origin, String translation){
        historyList.append(new HistoryEntry(origin,translation));
        historyCursor = historyList.size()-1;
    }

    public HistoryEntry previous(){
        historyCursor = Math.max(historyCursor - 1, 0);
        return historyList.get(historyCursor);
    }

    public HistoryEntry next(){
        historyCursor = Math.min(historyCursor+1,historyList.size()-1);
        return historyList.get(historyCursor);
    }

}
