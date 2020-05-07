package com.rubbertranslator.textread.clipboard.ocr;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OCRResult{

	@SerializedName("log_id")
	private long logId;

	@SerializedName("words_result")
	private List<WordsResultItem> wordsResult;

	@SerializedName("words_result_num")
	private int wordsResultNum;

	public long getLogId(){
		return logId;
	}

	public List<WordsResultItem> getWordsResult(){
		return wordsResult;
	}

	public int getWordsResultNum(){
		return wordsResultNum;
	}

	public String getCombinedWords(){
		StringBuilder words = new StringBuilder();
		for (WordsResultItem item: wordsResult){
			words.append(item.getWords());
		}
		return words.toString();
	}

	public static class WordsResultItem{

		@SerializedName("words")
		private String words;

		public String getWords(){
			return words;
		}
	}
}