package com.rubbertranslator.entity;
import java.util.Objects;

public class Pair<F,S> implements Comparable<Pair<F,S>> {
//    @SerializedName("first")
    protected F first;
//    @SerializedName("second")
    protected S second;

    public Pair() {
    }

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
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
        Pair<F,S> other = (Pair<F,S>) obj;
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
    public int compareTo(Pair<F,S> o) {
        if(Objects.equals(o,this)){
            return 0;
        }
        return -1;
    }

}
