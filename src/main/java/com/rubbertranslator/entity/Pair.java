package com.rubbertranslator.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Pair<T> implements Comparable<Pair<T>> {
//    @SerializedName("first")
    protected T first;
//    @SerializedName("second")
    protected T second;

    public Pair() {
    }

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair<T> other = (Pair<T>) obj;
        if (first != null && !first.equals(other.first)) {
            return false;
        }
        else return second == null || second.equals(other.second);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }

    @Override
    public int compareTo(Pair<T> o) {
        if(Objects.equals(o,this)){
            return 0;
        }
        return -1;
    }
}
