package com.rubbertranslator.modules.textprocessor.post;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * @author Raven
 * @version 1.0
 * date 2020/5/13 8:54
 */
public class WordsPair implements Comparable<WordsPair>{
    @SerializedName("src")
    private String src;
    @SerializedName("dest")
    private String dest;

    public WordsPair() {
    }

    public WordsPair(String src, String dest) {
        this.src = src;
        this.dest = dest;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WordsPair other = (WordsPair) obj;
        if (src != null && !src.equals(other.src)) {
            return false;
        }
        else if (dest != null && !dest.equals(other.dest)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + src.hashCode();
        result = 31 * result + dest.hashCode();
        return result;
    }

    @Override
    public int compareTo(WordsPair o) {
        if(Objects.equals(o,this)){
            return 0;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "WordsPair{" +
                "src='" + src + '\'' +
                ", dest='" + dest + '\'' +
                '}';
    }
}
