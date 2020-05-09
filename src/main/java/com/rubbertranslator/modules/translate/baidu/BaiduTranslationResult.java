package com.rubbertranslator.modules.translate.baidu;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaiduTranslationResult {
    @SerializedName("error_code")
    private Integer errorCode;

    @SerializedName("Invalid Access Limit")
    private String errorMsg;

    @SerializedName("trans_result")
    private List<TransResultItem> transResult;

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    public List<TransResultItem> getTransResult() {
        return transResult;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "BaiduTranslateResult{" +
                "transResult=" + transResult +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

    public static class TransResultItem {
        @SerializedName("dst")
        private String dst;

        @SerializedName("src")
        private String src;


        public String getDst() {
            return dst;
        }

        public String getSrc() {
            return src;
        }

        @Override
        public String toString() {
            return "TransResultItem{" +
                    "dst='" + dst + '\'' +
                    ", src='" + src + '\'' +
                    '}';
        }
    }

}
